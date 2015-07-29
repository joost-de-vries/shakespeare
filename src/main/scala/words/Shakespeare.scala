package words

import java.net.URL
import java.nio.ByteBuffer
import java.nio.channels.{AsynchronousFileChannel, CompletionHandler}
import java.nio.charset.StandardCharsets
import java.nio.file.attribute.FileAttribute
import java.nio.file.{Files, Paths, StandardOpenOption}
import java.util.Collections
import java.util.concurrent.{AbstractExecutorService, Future => JavaFuture, TimeUnit}

import akka.stream.scaladsl.{FlattenStrategy, Source}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService, Future, Promise}
import scala.io.Source._
import scala.io.{Source => ScalaSource}

/**
 * Created by joost1 on 22/07/15.
 */
object Shakespeare {
  val filePath = "/shakespeare - works.txt"

  def source:ScalaSource = fromInputStream(getClass.getResourceAsStream(filePath))

  def url:URL = getClass.getResource(filePath)

  val endOfInitialLicense = 170

  val startOfFinalLicense = 124452

  def isDramatisStart(line: String): Boolean =
    line.toLowerCase.startsWith("dramatis personae")


  def isDramatisEnd(line: String): Boolean =
    line.startsWith("<<THIS ELECTRONIC VERSION")

  def readFile()(implicit executionContext: ExecutionContext): IndexedSeq[Future[String]] = {
    import scala.collection.JavaConversions._
    val path = Paths.get(url.toURI)

    val size = Files.size(path).toInt
    val attributes = Array[FileAttribute[_]]()

    val channel = AsynchronousFileChannel.open(path, Set(StandardOpenOption.READ),
      ExecutionContextExecutorServiceBridge(executionContext), attributes: _*)


    val blockSize = size / 100000
    val nrOfBlocks = size / blockSize
    val blocks = 0 until nrOfBlocks
    val remainder = size % blockSize
    val futures = blocks.map(i => readBuffer(channel,
      from = i * blockSize, bytes = blockSize))
    futures :+ readBuffer(channel, from = (nrOfBlocks - 1) * blockSize, remainder)
  }

  def readStream()(implicit executionContext: ExecutionContext): Source[String, Unit] = {
    val x = readFile().map(f => Source(f))
    Source(() => x.iterator).flatten[String](FlattenStrategy.concat)
  }

  private def readStreamx()(implicit executionContext: ExecutionContext): Source[String, Unit] = {
    //leads to a stack overflow error at 1000 elements because of non tailrecursive function in Akka
    readFile().map(f => Source(f)).foldLeft(Source.empty[String])((accSource: Source[String, Unit], s) => accSource.concatMat(s)((_, _) => ()))
  }

  //???
  def readBuffer(channel: AsynchronousFileChannel, from: Int, bytes: Int): Future[String] = {
    def toUtf8(buffer: ByteBuffer): String = new String(buffer.array(), StandardCharsets.UTF_8)

    val buffer = ByteBuffer.allocate(bytes);

    val p = Promise[String]()

    val handler = new CompletionHandler[Integer, String]() {
      override def completed(bytesRead: Integer, attachment: String): Unit = {
        p.success(toUtf8(buffer))
      }

      override def failed(exc: Throwable, attachment: String): Unit = {
        p.failure(exc)
      }
    }
    channel.read(buffer, from, "", handler)
    p.future
  }
}

object ExecutionContextExecutorServiceBridge {
  def apply(ec: ExecutionContext): ExecutionContextExecutorService = ec match {
    case null => throw null  //scalastyle:ignore null
    case eces: ExecutionContextExecutorService => eces
    case other => new AbstractExecutorService with ExecutionContextExecutorService {
      override def prepare(): ExecutionContext = other

      override def isShutdown = false

      override def isTerminated = false

      override def shutdown() = ()

      override def shutdownNow() = Collections.emptyList[Runnable]

      override def execute(runnable: Runnable): Unit = other execute runnable

      override def reportFailure(t: Throwable): Unit = other reportFailure t

      override def awaitTermination(length: Long, unit: TimeUnit): Boolean = false
    }
  }
}

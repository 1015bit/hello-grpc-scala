/*
 * Copyright 2016 Petra Bierleutgeb
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.ontherocks.hellogrpc
package clock

protected object ClockServer {
  private val Port = 50051
}

protected class ClockServer() {

  import io.grpc.{ Server, ServerBuilder }
  import scala.concurrent.ExecutionContext

  private var server: Option[Server] = None

  def start(): Unit = {
    server = Option(
      ServerBuilder
        .forPort(ClockServer.Port)
        .addService(ClockGrpc.bindService(new ClockService, ExecutionContext.global))
        .build
        .start
    )
    println(s"SumServer started. Listening on port ${ClockServer.Port}")

    // make sure our server is stopped when jvm is shut down
    Runtime.getRuntime.addShutdownHook(new Thread() {
      override def run(): Unit = stopServer()
    })
  }

  private def stopServer(): Unit = server.foreach(_.shutdown())

  def blockUntilShutdown(): Unit =
    server.foreach(_.awaitTermination())

}

object ClockServerApp {

  def main(args: Array[String]): Unit = {
    val server = new ClockServer()
    server.start()
    server.blockUntilShutdown()
  }

}

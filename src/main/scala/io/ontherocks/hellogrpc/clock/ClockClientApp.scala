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

import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver
import io.ontherocks.hellogrpc.clock.ClockGrpc.{ ClockBlockingStub, ClockStub }

import scala.concurrent.{ Await, Promise }
import scala.concurrent.duration._

object ClockClientApp {

  private val Host = "localhost"
  private val Port = 50051

  private val channel = ManagedChannelBuilder.forAddress(Host, Port).usePlaintext(true).build
  private val request = TimeRequest()

  def main(args: Array[String]): Unit = {
    // --- blocking/synchronous call ---
    val blockingClockClient: ClockBlockingStub = ClockGrpc.blockingStub(channel)

    // remaining code will be executed AFTER stream/iterator has completed
    val blockingClockResponse: Iterator[TimeResponse] = blockingClockClient.getTime(request)
    for (t <- blockingClockResponse) {
      println(s"[blocking client] received: $t")
    }

    // --- non-blocking/asynchronous call ---
    val asyncClockClient: ClockStub = ClockGrpc.stub(channel)
    val streamCompleted             = Promise[Unit]()

    // calling getTime and registering an observer will NOT block the current thread
    val timeResponseObserver = new StreamObserver[TimeResponse] {

      def onNext(value: TimeResponse): Unit = println(s"[async client] received: $value")

      def onError(t: Throwable): Unit = println(s"[async client] error: $t")

      def onCompleted(): Unit = {
        streamCompleted.success(())
        println("[async client] stream completed")
      }

    }
    asyncClockClient.getTime(request, timeResponseObserver)

    // prevents application from shutting down before stream has completed
    Await.ready(streamCompleted.future, Duration.Inf)
  }

}

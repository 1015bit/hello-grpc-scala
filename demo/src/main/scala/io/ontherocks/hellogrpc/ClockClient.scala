/*
 * Copyright 2017 Petra Bierleutgeb
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

import java.util.concurrent.TimeUnit

import io.grpc.{ ManagedChannel, ManagedChannelBuilder }
import io.grpc.stub.StreamObserver
import io.ontherocks.hellogrpc.clock.{ ClockGrpc, TimeRequest, TimeResponse }

import scala.concurrent.{ Await, Promise }
import scala.concurrent.duration._
import scala.util.{ Failure, Success, Try }

object ClockClient {

  def main(args: Array[String]): Unit = {
    val channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext(true).build
    val client  = ClockGrpc.stub(channel)

    def observer(p: Promise[String]): StreamObserver[TimeResponse] =
      new StreamObserver[TimeResponse] {
        def onError(t: Throwable): Unit = {
          println(s"ON_ERROR: $t")
          p.complete(tryAwaitTermination(channel, "received onError"))
        }
        def onCompleted(): Unit = {
          println("ON_COMPLETED")
          p.complete(tryAwaitTermination(channel, "received onComplete"))
        }
        def onNext(response: TimeResponse): Unit =
          println(s"ON_NEXT: Received current time ms: ${response.currentTime}")
      }

    val terminated = Promise[String]
    client.streamTime(TimeRequest(), observer(terminated))

    val shutdownReason = Await.result(terminated.future, Duration.Inf)
    println(s"Successfully shutdown application. Reason: $shutdownReason")
  }

  private def tryAwaitTermination(channel: ManagedChannel, reason: String): Try[String] = {
    channel.shutdown()
    if (channel.awaitTermination(5, TimeUnit.SECONDS)) {
      Success(reason)
    } else {
      Failure(new RuntimeException("Could not terminate channel."))
    }
  }

}

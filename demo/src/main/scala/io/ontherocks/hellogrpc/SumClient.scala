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

import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver
import io.ontherocks.hellogrpc.sum.{ SumGrpc, SumRequest, SumResponse }
import monix.execution.Scheduler.{ global => scheduler }

import scala.concurrent.duration._
import scala.util.Random

object SumClient {

  def main(args: Array[String]): Unit = {
    val channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext(true).build
    val client  = SumGrpc.stub(channel)

    val responseObserver = new StreamObserver[SumResponse] {
      def onError(t: Throwable): Unit = println(s"ON_ERROR: $t")
      def onCompleted(): Unit         = println("ON_COMPLETED")
      def onNext(value: SumResponse): Unit =
        println(s"ON_NEXT: Current sum: ${value.currentResult}")
    }

    val requestObserver = client.add(responseObserver)

    scheduler.scheduleWithFixedDelay(0.seconds, 5.seconds) {
      val toBeAdded = Random.nextInt(11)
      println(s"Adding number: $toBeAdded")
      requestObserver.onNext(SumRequest(toBeAdded))
    }
  }

}

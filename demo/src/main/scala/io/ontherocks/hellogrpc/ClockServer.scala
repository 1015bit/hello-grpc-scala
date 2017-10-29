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

import io.grpc.stub.StreamObserver
import io.ontherocks.hellogrpc.clock.ClockGrpc.Clock
import io.ontherocks.hellogrpc.clock.{ ClockGrpc, TimeRequest, TimeResponse }
import monix.execution.Scheduler
import monix.execution.Scheduler.{ global => scheduler }

import scala.concurrent.duration._

object ClockServer extends GrpcServer {

  class ClockService extends Clock {
    def streamTime(request: TimeRequest, responseObserver: StreamObserver[TimeResponse]): Unit =
      scheduler.scheduleWithFixedDelay(0.seconds, 3.seconds) {
        responseObserver.onNext(TimeResponse(System.currentTimeMillis()))
      }
  }

  def main(args: Array[String]): Unit = {
    val ssd = ClockGrpc.bindService(new ClockService(), Scheduler.global)
    runServer(ssd)
  }

}

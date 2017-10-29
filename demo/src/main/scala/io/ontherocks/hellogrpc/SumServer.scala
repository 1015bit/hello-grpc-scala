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
import io.ontherocks.hellogrpc.sum.{ SumGrpc, SumRequest, SumResponse }
import monix.execution.atomic.{ Atomic, AtomicInt }

import scala.concurrent.ExecutionContext

object SumServer extends GrpcServer {

  class SumService extends SumGrpc.Sum {
    def add(responseObserver: StreamObserver[SumResponse]): StreamObserver[SumRequest] =
      new StreamObserver[SumRequest] {
        val currentSum: AtomicInt       = Atomic(0) // beware: stream observer is not thread-safe
        def onError(t: Throwable): Unit = println(s"ON_ERROR: $t")
        def onCompleted(): Unit         = println("ON_COMPLETED")
        def onNext(value: SumRequest): Unit = {
          println(s"ON_NEXT: adding value ${value.toAdd}")
          responseObserver.onNext(SumResponse(currentSum.addAndGet(value.toAdd)))
        }
      }
  }

  def main(args: Array[String]): Unit = {
    val ssd = SumGrpc.bindService(new SumService(), ExecutionContext.global)
    runServer(ssd)
  }

}

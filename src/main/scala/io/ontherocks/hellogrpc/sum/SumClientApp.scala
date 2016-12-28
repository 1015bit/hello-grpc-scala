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
package sum

import io.grpc.ManagedChannelBuilder
import io.ontherocks.hellogrpc.sum.SumGrpc.{ SumBlockingStub, SumStub }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{ Failure, Success }

object SumClientApp {

  private val Host = "localhost"
  private val Port = 50051

  private val channel = ManagedChannelBuilder.forAddress(Host, Port).usePlaintext(true).build
  private val request = SumRequest(3, 4)

  def main(args: Array[String]): Unit = {
    // blocking/synchronous call
    val blockingSumClient: SumBlockingStub = SumGrpc.blockingStub(channel)
    val blockingSumResponse: SumResponse   = blockingSumClient.calcSum(request)
    println(s"Blocking sum client response: $blockingSumResponse")

    // non-blocking/asynchronous call
    val asyncSumClient: SumStub               = SumGrpc.stub(channel)
    val asyncSumResponse: Future[SumResponse] = asyncSumClient.calcSum(request)
    asyncSumResponse.onComplete {
      case Success(sumResponse) => println(s"Async sum client response: $sumResponse")
      case Failure(e)           => println(s"Error while calling sum service: $e")
    }
  }

}

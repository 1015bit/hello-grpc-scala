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

package io.ontherocks.hellogrpc.clock

import io.grpc.stub.StreamObserver
import io.ontherocks.hellogrpc.HelloGrpcBaseSpec
import org.scalatest.time.{ Seconds, Span }

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Promise

object ClockServiceSpec {
  val RepeatForSeconds = 3
}

class ClockServiceSpec extends HelloGrpcBaseSpec {

  implicit val patienceConf = PatienceConfig(timeout = Span(7, Seconds))

  import ClockServiceSpec._
  val clockService = new ClockService(RepeatForSeconds)

  "ClockService" should {
    "return the current time in ms every second for the next ten seconds" in {
      def invokeClockService(): Promise[Seq[Long]] = {
        val p                 = Promise[Seq[Long]]
        val responseCollector = new ArrayBuffer[Long]()
        val responseObserver: StreamObserver[TimeResponse] = new StreamObserver[TimeResponse] {
          def onError(t: Throwable)              = throw t
          def onCompleted()                      = p.success(responseCollector.toList)
          def onNext(timeResponse: TimeResponse) = responseCollector += timeResponse.currentTime
        }
        clockService.getTime(TimeRequest(), responseObserver)
        p
      }

      val beforeStartMs = System.currentTimeMillis()

      val collectedResponsesPromise = invokeClockService()

      whenReady(collectedResponsesPromise.future) { collectedResponses =>
        val afterCompletionMs = System.currentTimeMillis()
        collectedResponses.size shouldEqual RepeatForSeconds + 1
        assert(
          collectedResponses.forall(
            timeMs => (beforeStartMs <= timeMs && timeMs <= afterCompletionMs)
          )
        )
        collectedResponses shouldBe sorted
        collectedResponses.toSet.size shouldEqual collectedResponses.size
      }
    }
  }

}

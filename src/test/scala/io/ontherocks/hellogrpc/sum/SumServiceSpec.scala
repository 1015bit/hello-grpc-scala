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

package io.ontherocks.hellogrpc.sum

import io.ontherocks.hellogrpc.HelloGrpcBaseSpec

class SumServiceSpec extends HelloGrpcBaseSpec {

  val sumService = new SumService()

  "SumService" should {
    "return the sum of two given numbers" in {
      forAll { (a: Int, b: Int) =>
        whenReady(sumService.calcSum(SumRequest(a, b))) { response =>
          response.result shouldBe (a + b)
        }
      }
    }
  }

}

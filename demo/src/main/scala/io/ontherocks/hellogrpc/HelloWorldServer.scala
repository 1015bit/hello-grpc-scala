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

import io.ontherocks.hellogrpc.helloworld.{ Greeting, HelloWorldGrpc, ToBeGreeted }

import scala.concurrent.{ ExecutionContext, Future }

object HelloWorldServer extends GrpcServer {

  class HelloWorldService extends HelloWorldGrpc.HelloWorld {
    def sayHello(request: ToBeGreeted): Future[Greeting] = {
      val greetedPerson = request.person match {
        case Some(person) => person.name
        case None         => "anonymous"
      }
      Future.successful(Greeting(message = s"Hello ${greetedPerson}!"))
    }
  }

  def main(args: Array[String]): Unit = {
    val ssd = HelloWorldGrpc.bindService(new HelloWorldService(), ExecutionContext.global)
    runServer(ssd)
  }

}

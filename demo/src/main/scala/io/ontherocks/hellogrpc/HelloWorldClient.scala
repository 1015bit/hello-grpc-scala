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
import io.ontherocks.hellogrpc.helloworld.HelloWorldGrpc.{ HelloWorldBlockingStub, HelloWorldStub }
import io.ontherocks.hellogrpc.helloworld.ToBeGreeted.Person
import io.ontherocks.hellogrpc.helloworld.{ Greeting, HelloWorldGrpc, ToBeGreeted }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object HelloWorldClient {

  def main(args: Array[String]): Unit = {
    val channel = ManagedChannelBuilder
      .forAddress("localhost", 50051) // host and port of service
      .usePlaintext(true) // don't use encryption (for demo purposes)
      .build

    val person = Person(
      name = "Bob"
    )

    val toBeGreeted = ToBeGreeted(Some(person))
    // or use the generated builder methods
    // val toBeGreeted2 = ToBeGreeted().withPerson(Person(name = "Bob"))

    // async client
    val stub: HelloWorldStub        = HelloWorldGrpc.stub(channel)
    val greetingF: Future[Greeting] = stub.sayHello(toBeGreeted)

    greetingF.foreach(response => println(s"ASYNC RESULT: ${response.message}"))

    // beware: blocking code below
    val blockingStub: HelloWorldBlockingStub = HelloWorldGrpc.blockingStub(channel)
    val greeting: Greeting                   = blockingStub.sayHello(toBeGreeted)

    println(s"SYNC(BLOCKING) RESULT: ${greeting.message}")
  }

}

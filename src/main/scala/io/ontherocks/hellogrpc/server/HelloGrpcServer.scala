package io.ontherocks.hellogrpc.server

import io.grpc.ServerServiceDefinition
import io.ontherocks.hellogrpc.clock.{ClockGrpc, ClockService}

object HelloGrpcServer {
  private val Port = 50051
}

class HelloGrpcServer(serverServiceDefinition: ServerServiceDefinition) {

  import io.grpc.{ Server, ServerBuilder }

  private var server: Option[Server] = None

  def start(): Unit = {
    server = Option(
      ServerBuilder
        .forPort(HelloGrpcServer.Port)
        .addService(serverServiceDefinition)
        .build
        .start
    )
    println(s"Server started. Listening on port ${HelloGrpcServer.Port}")

    // make sure our server is stopped when jvm is shut down
    Runtime.getRuntime.addShutdownHook(new Thread() {
      override def run(): Unit = stopServer()
    })
  }

  private def stopServer(): Unit = server.foreach(_.shutdown())

  def blockUntilShutdown(): Unit =
    server.foreach(_.awaitTermination())

}
package io.vertx.grpc;

import io.grpc.BindableService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.grpc.ServiceGrpc.ServiceVertxImplBase;

public class Server extends AbstractVerticle {

  @Override
  public void start() {

    BindableService bindableService = new ServiceVertxImplBase() {

      @Override
      public void connect(GrpcReadStream<ClientRequest> request, Future<ClientResponse> response) {
        request.handler(clientRequest -> {
          System.out.println(clientRequest.getId());
        }).endHandler(v -> {
          System.out.println("Stream end");
          response.complete(ClientResponse.newBuilder().setBody("Server connected").build());
        });
      }

    };

    VertxServer rpcServer = VertxServerBuilder
        .forPort(vertx, 8080)
        .addService(bindableService)
        .build();

    rpcServer.start(result -> {
      if (result.succeeded()) {
        System.out.println("Server started at 8080");
      } else {
        System.out.println("Fail to start server " + result.cause().getMessage());
      }
    });

  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new Server());
  }

}
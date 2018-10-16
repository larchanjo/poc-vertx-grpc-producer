package io.vertx.grpc;

import io.grpc.ManagedChannel;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.grpc.ServiceGrpc.ServiceVertxStub;
import java.util.UUID;
import org.jruby.RubyProcess.Sys;

public class Consumer extends AbstractVerticle {

  @Override
  public void start() {

    ManagedChannel channel = VertxChannelBuilder
        .forAddress(vertx, "localhost", 8080)
        .usePlaintext(true)
        .build();

    ServiceVertxStub stub = ServiceGrpc.newVertxStub(channel);

    stub.connect(stream -> {
      stream.handler(serverResponse -> {
        if (serverResponse.succeeded()) {
          System.out.println("Server replied OK");
        } else {
          System.out.println("Server replied NOK");
        }
      });

      vertx.setPeriodic(1000, handler -> {
        stream.write(ClientRequest.newBuilder().setId(UUID.randomUUID().toString()).build());
      });
    });

  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new Consumer());
  }

}
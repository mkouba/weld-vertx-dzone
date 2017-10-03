package org.jboss.weld.vertx.dzone;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.jboss.weld.vertx.web.WebRoute;

import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

@ApplicationScoped
public class HelloEventBus {

    @WebRoute("/hello-event-bus")
    void hello(@Observes RoutingContext ctx, Vertx vertx) {
        // Send the message via event bus and process the reply asynchronously
        vertx.eventBus().send("name.service.address", ctx.request().getParam("name"), r -> {
            if (r.succeeded())
                ctx.response().setStatusCode(200).end("Hello " + r.result().body().toString() + "!");
            else
                ctx.fail(r.cause());
        });
    }

}

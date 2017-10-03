/*
 * JBoss, Home of Professional Open Source
 * Copyright 2017, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.weld.vertx.dzone;

import static org.junit.Assert.assertEquals;

import org.jboss.weld.vertx.web.WeldWebVerticle;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class HelloTest {

    @Rule
    public Timeout globalTimeout = Timeout.millis(10000);

    private Vertx vertx;

    @Before
    public void init(TestContext context) throws InterruptedException {
        vertx = Vertx.vertx();
        Async async = context.async();
        WeldWebVerticle weldVerticle = new WeldWebVerticle();
        vertx.deployVerticle(weldVerticle, deploy -> {
            if (deploy.succeeded()) {
                // Configure the router after Weld bootstrap finished
                vertx.createHttpServer().requestHandler(weldVerticle.createRouter()::accept).listen(8080, (listen) -> {
                    if (listen.succeeded()) {
                        async.complete();
                    } else {
                        context.fail(listen.cause());
                    }
                });
            } else {
                context.fail(deploy.cause());
            }
        });
    }

    @After
    public void close(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testHelloHandler() {
        test("/hello-handler");
    }

    @Test
    public void testHelloObserver() {
        test("/hello-observer");
    }

    @Test
    public void testHelloEventBus() {
        test("/hello-event-bus");
    }

    private void test(String pathBase) {
        Response response = RestAssured.get(pathBase);
        response.then().assertThat().statusCode(200);
        assertEquals("Hello courageous developer!", response.asString());
        response = RestAssured.get(pathBase + "?name=foo");
        response.then().assertThat().statusCode(200);
        assertEquals("Hello foo!", response.asString());
    }

}

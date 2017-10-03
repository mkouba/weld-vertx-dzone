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

import org.jboss.weld.vertx.web.WeldWebVerticle;

import io.vertx.core.Vertx;

public class MyVertxApp {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        WeldWebVerticle weldVerticle = new WeldWebVerticle();
        vertx.deployVerticle(weldVerticle, result -> {
            if (result.succeeded()) {
                vertx.createHttpServer().requestHandler(weldVerticle.createRouter()::accept).listen(8080);
            } else {
                throw new IllegalStateException("Weld verticle failure: " + result.cause());
            }
        });
    }
}

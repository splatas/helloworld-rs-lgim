/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
package org.jboss.as.quickstarts.rshelloworld;

// import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

// import javax.ws.rs.ApplicationPath;
// import javax.ws.rs.core.Application;

/**
 * A simple REST service which is able to say hello to someone using HelloService Please take a look at the web.xml where JAX-RS
 * is enabled
 *
 * @author gbrey@redhat.com
 *
 */
@Path("/")
public class HelloWorld {
// @ApplicationPath("/")
// public class HelloWorld extends Application{
    // @Inject
    // HelloService helloService;

    // @GET
    // @Path("/json")
    // @Produces({ "application/json" })
    // public String getHelloWorldJSON() {
    //     return "{\"result\":\"" + helloService.createHelloMessage("World") + "\"}";
    // }

    // @GET
    // @Path("/xml")
    // @Produces({ "application/xml" })
    // public String getHelloWorldXML() {
    //     return "<xml><result>" + helloService.createHelloMessage("World") + "</result></xml>";
    // }

    // ----------------------------- //
    @GET
    @Path("/public")
    @Produces(MediaType.TEXT_PLAIN)
    public String publicEndpoint() {
      return "public";
    }

    @GET
    @Path("/secure")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed("USER")
    public String secureEndpoint(@Context SecurityContext ctx) {
      return "user=" + ctx.getUserPrincipal().getName();
    }
}

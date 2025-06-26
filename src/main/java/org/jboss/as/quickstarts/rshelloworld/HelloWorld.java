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

import org.jboss.as.quickstarts.rshelloworld.model.Hello;
// import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.Optional;

@Path("/")
public class HelloWorld {

    // ----------------------------- //
    @GET
    @Path("/public")
    @Produces(MediaType.TEXT_PLAIN)
    public String publicEndpoint() {
      return getGreeting("public");
    }

    @GET
    @Path("/secure")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed("USER")
    public String secureEndpoint(@Context SecurityContext ctx) {
      // return "user=" + ctx.getUserPrincipal().getName();
      return getGreeting("secure");
    }

    @PersistenceContext(unitName = "helloworldPU")
    private EntityManager em;

    public String getGreeting(String type) {
        TypedQuery<Hello> query = em.createQuery(
            "SELECT h FROM Hello h WHERE h.service = :service", Hello.class);
        query.setParameter("service", type);

        try {
            Hello result = query.setMaxResults(1).getSingleResult();
            return result.getResponse();

        } catch (NoResultException e) {
            return "Service not found";
        }
    }
}

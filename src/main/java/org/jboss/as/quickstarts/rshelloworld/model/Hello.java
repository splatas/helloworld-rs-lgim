package org.jboss.as.quickstarts.rshelloworld.model;

import javax.persistence.*;

@Entity
@Table(name = "hello")
public class Hello {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String service;

    private String response;

    // Getters y setters
    public int getId() {
        return id;
    }

    public String getService() {
        return service;
    }

    public String getResponse() {
        return response;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setService(String service) {
        this.service = service;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}

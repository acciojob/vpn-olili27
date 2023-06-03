package com.driver.model;

import javax.persistence.*;

@Entity
public class Connection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn
    private User user;

    @ManyToOne
    @JoinColumn
    private ServiceProvider serviceProvider;

    public Connection() {
    }

    public Connection(Integer id, User user, ServiceProvider serviceProvider) {
        this.id = id;
        this.user = user;
        this.serviceProvider = serviceProvider;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }
}

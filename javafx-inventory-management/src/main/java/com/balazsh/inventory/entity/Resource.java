package com.balazsh.inventory.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Resource", schema = "new")
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resource_id", nullable = false)
    private Integer id;

    @Column(name = "resource_name", length = 50)
    private String resourceName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

}
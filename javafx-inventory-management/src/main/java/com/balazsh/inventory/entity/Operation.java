package com.balazsh.inventory.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Operation", schema = "new")
public class Operation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "operation_id", nullable = false)
    private Integer id;

    @Column(name = "operation_name", length = 20)
    private String operationName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

}
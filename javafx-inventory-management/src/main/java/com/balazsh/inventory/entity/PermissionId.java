package com.balazsh.inventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.util.Objects;

@Embeddable
public class PermissionId implements java.io.Serializable {
    private static final long serialVersionUID = 7208159064027312556L;
    @Column(name = "operation_id", nullable = false)
    private Integer operationId;

    @Column(name = "resource_id", nullable = false)
    private Integer resourceId;

    public Integer getOperationId() {
        return operationId;
    }

    public void setOperationId(Integer operationId) {
        this.operationId = operationId;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PermissionId entity = (PermissionId) o;
        return Objects.equals(this.resourceId, entity.resourceId) &&
                Objects.equals(this.operationId, entity.operationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceId, operationId);
    }

}
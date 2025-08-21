module com.balazsh.inventory {
    requires javafx.controls;
    requires org.hibernate.orm.core;
    requires java.naming;
    requires org.slf4j;
    requires org.jboss.logging;
    requires com.fasterxml.classmate;
    requires net.bytebuddy;
    requires jakarta.persistence;
    requires com.fasterxml.jackson.databind;

    opens com.balazsh.inventory.entity;
    opens com.balazsh.inventory.entity.json;
    opens com.balazsh.inventory.dao;
    opens com.balazsh.inventory.util;
    opens com.balazsh.inventory.domain.model to javafx.base;
    opens com.balazsh.inventory.features.dashboard;
    opens com.balazsh.inventory.features.authentication;
    exports com.balazsh.inventory to javafx.graphics;
}
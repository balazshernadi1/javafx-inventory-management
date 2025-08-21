package com.balazsh.inventory.entity.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class UniqueAttributes {

    @JsonProperty("attributes")
    List<Attribute> attributeList = new ArrayList<>();

    @JsonProperty("components")
    List<Component> componentList = new ArrayList<>();

    public UniqueAttributes() {

    }

    public List<Attribute> getAttributeList() {
        return attributeList;
    }

    public void setAttributeList(List<Attribute> attributeList) {
        this.attributeList = attributeList;
    }

    public List<Component> getComponentList() {
        return componentList;
    }

    public void setComponentList(List<Component> componentList) {
        this.componentList = componentList;
    }
}

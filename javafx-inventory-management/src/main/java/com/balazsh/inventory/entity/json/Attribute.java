package com.balazsh.inventory.entity.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Attribute {

    @JsonProperty("name")
    private String attributeName;

    @JsonProperty("value")
    private Object attributeValue;

    @JsonProperty("unit")
    private String unit;

    @JsonProperty("data_type")
    private String dataType;

    public Attribute() {
    }

    public Attribute(String attributeName, Object attributeValue, String unit, String dataType) {
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
        this.unit = unit;
        this.dataType = dataType;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public Object getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(Object attributeValue) {
        this.attributeValue = attributeValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}

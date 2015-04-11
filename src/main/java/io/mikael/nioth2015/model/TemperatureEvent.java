package io.mikael.nioth2015.model;

import java.time.ZonedDateTime;

public class TemperatureEvent {

    public String unitOfMeasure;

    public String partitionId;

    public String measurementType;

    public ZonedDateTime timeCreated;

    public String organization;

    public String guid;

    public double value;

    public String sensorId;

    public String modCamId;

    public TemperatureEvent() {
    }

    public TemperatureEvent(final String unitOfMeasure, final String partitionId, final String measurementType, final ZonedDateTime timeCreated, final String organization, final String guid, final double value, final String sensorId, final String modCamId) {
        this.setUnitOfMeasure(unitOfMeasure);
        this.setPartitionId(partitionId);
        this.setMeasurementType(measurementType);
        this.setTimeCreated(timeCreated);
        this.setOrganization(organization);
        this.setGuid(guid);
        this.setValue(value);
        this.setSensorId(sensorId);
        this.setModCamId(modCamId);
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public String getPartitionId() {
        return partitionId;
    }

    public void setPartitionId(String partitionId) {
        this.partitionId = partitionId;
    }

    public String getMeasurementType() {
        return measurementType;
    }

    public void setMeasurementType(String measurementType) {
        this.measurementType = measurementType;
    }

    public ZonedDateTime getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(ZonedDateTime timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public String getModCamId() {
        return modCamId;
    }

    public void setModCamId(String modCamId) {
        this.modCamId = modCamId;
    }
}

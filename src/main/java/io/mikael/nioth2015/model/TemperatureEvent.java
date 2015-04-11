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
        this.unitOfMeasure = unitOfMeasure;
        this.partitionId = partitionId;
        this.measurementType = measurementType;
        this.timeCreated = timeCreated;
        this.organization = organization;
        this.guid = guid;
        this.value = value;
        this.sensorId = sensorId;
        this.modCamId = modCamId;
    }
}

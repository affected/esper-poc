package io.mikael.nioth2015.model;

import java.time.ZonedDateTime;

public class TemperatureEvent {

    public final String unitOfMeasure;

    public final String partitionId;

    public final String measurementType;

    public final ZonedDateTime timeCreated;

    public final String organization;

    public final String guid;

    public final double value;

    public final String sensorId;

    public final String modCamId;

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

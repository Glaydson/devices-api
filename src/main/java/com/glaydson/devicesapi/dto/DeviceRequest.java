package com.glaydson.devicesapi.dto;

import com.glaydson.devicesapi.model.Device;
import lombok.Data;

@Data
public class DeviceRequest {
    private String name;
    private String brand;
    private Device.State state;

    public DeviceRequest(String name, String brand, Device.State state) {
        this.name = name;
        this.brand = brand;
        this.state = state;
    }
}
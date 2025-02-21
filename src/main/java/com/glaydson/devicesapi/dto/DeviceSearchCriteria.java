package com.glaydson.devicesapi.dto;

import com.glaydson.devicesapi.model.Device;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
// At this time, we are searching only by brand and state
public class DeviceSearchCriteria {
    private String brand;
    private Device.State state;

}
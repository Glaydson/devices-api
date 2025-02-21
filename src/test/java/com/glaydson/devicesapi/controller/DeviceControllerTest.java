// src/test/java/com/glaydson/devicesapi/controller/DeviceControllerTest.java
package com.glaydson.devicesapi.controller;

import com.glaydson.devicesapi.dto.DeviceRequest;
import com.glaydson.devicesapi.dto.DeviceSearchCriteria;
import com.glaydson.devicesapi.exception.DeviceInUseException;
import com.glaydson.devicesapi.exception.GlobalExceptionHandler;
import com.glaydson.devicesapi.exception.ResourceNotFoundException;
import com.glaydson.devicesapi.model.Device;
import com.glaydson.devicesapi.repository.DeviceRepository;
import com.glaydson.devicesapi.service.DeviceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DeviceControllerTest {

    public static final String BASE_PATH = "/api/v1/devices";
    private MockMvc mockMvc;

    @Mock
    private DeviceService deviceService;

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DeviceController deviceController;

    private DeviceRequest device1;
    private DeviceRequest device2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(deviceController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        device1 = new DeviceRequest("Device1", "Brand1", Device.State.AVAILABLE);
        device2 = new DeviceRequest("Device2", "Brand2", Device.State.IN_USE);
    }

    @Test
    void testCreateDevice() throws Exception {
        Device device = new Device(1L, device1.getName(), device1.getBrand(), device1.getState(), LocalDateTime.now());
        when(deviceService.createDevice(any(DeviceRequest.class))).thenReturn(device);

        mockMvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Device1\",\"brand\":\"Brand1\",\"state\":\"AVAILABLE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Device1"))
                .andExpect(jsonPath("$.brand").value("Brand1"));
    }

    @Test
    void testGetDeviceById() throws Exception {
        Device device = new Device(1L, device1.getName(), device1.getBrand(), device1.getState(), LocalDateTime.now());

        when(deviceService.getDeviceById(anyLong())).thenReturn(device);

        mockMvc.perform(get(BASE_PATH + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Device1"))
                .andExpect(jsonPath("$.brand").value("Brand1"));
    }

    @Test
    void testGetDeviceByIdNotFound() throws Exception {

        when(deviceService.getDeviceById(anyLong())).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get(BASE_PATH + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(ResourceNotFoundException.class, result.getResolvedException()));
    }

    @Test
    void testGetAllDevices() throws Exception {
        Device device = new Device(1L, device1.getName(), device1.getBrand(), device1.getState(), LocalDateTime.now());
        when(deviceService.getAllDevices()).thenReturn(List.of(device));

        mockMvc.perform(get(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Device1"))
                .andExpect(jsonPath("$[0].brand").value("Brand1"));
    }

    @Test
    void testSearchByBrandAndState() throws Exception {
        Device device = new Device(1L, device1.getName(), device1.getBrand(), device1.getState(), LocalDateTime.now());
        when(deviceService.getDevicesByCriteria(any(DeviceSearchCriteria.class))).thenReturn(List.of(device));

        mockMvc.perform(get(BASE_PATH + "/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"brand\":\"Brand1\",\"state\":\"AVAILABLE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Device1"))
                .andExpect(jsonPath("$[0].brand").value("Brand1"));
    }

    @Test
    void testUpdateDevice() throws Exception {
        Device expectedDevice = new Device(1L, "UpdatedName", "Brand2", Device.State.AVAILABLE, LocalDateTime.now());
        Device existingDevice = new Device(1L, "Device1", "Brand1", Device.State.AVAILABLE, LocalDateTime.now());
        when(deviceService.getDeviceById(anyLong())).thenReturn(existingDevice);
        when(deviceService.updateDevice(anyLong(), any(DeviceRequest.class))).thenReturn(expectedDevice);

        mockMvc.perform(put(BASE_PATH + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"UpdatedName\",\"brand\":\"Brand2\",\"state\":\"AVAILABLE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UpdatedName"))
                .andExpect(jsonPath("$.brand").value("Brand2"));
    }

    @Test
    void testUpdateDeviceInUse() throws Exception {
        Device existingDevice = new Device(1L, "Device1", "Brand1", Device.State.IN_USE, LocalDateTime.now());
        when(deviceService.getDeviceById(anyLong())).thenReturn(existingDevice);
        when(deviceService.updateDevice(anyLong(), any(DeviceRequest.class))).thenThrow(new DeviceInUseException("Name and brand cannot be updated if the device is in use"));

        mockMvc.perform(put(BASE_PATH + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Device1\",\"brand\":\"New Brand\",\"state\":\"IN_USE\",\"creationTime\":\"" + existingDevice.getCreationTime() + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(DeviceInUseException.class, result.getResolvedException()));
    }

    @Test
    void testDeleteDeviceInUse() throws Exception {
        Device existingDevice = new Device(1L, "Device1", "Brand1", Device.State.IN_USE, LocalDateTime.now());
        when(deviceService.getDeviceById(anyLong())).thenReturn(existingDevice);
        doThrow(new DeviceInUseException("Devices in use cannot be removed")).when(deviceService).deleteDevice(anyLong());

        mockMvc.perform(delete(BASE_PATH + "/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(DeviceInUseException.class, result.getResolvedException()));
    }
}
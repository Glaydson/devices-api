// src/test/java/com/glaydson/devicesapi/controller/DeviceControllerTest.java
package com.glaydson.devicesapi.controller;

import com.glaydson.devicesapi.exception.DeviceInUseException;
import com.glaydson.devicesapi.exception.GlobalExceptionHandler;
import com.glaydson.devicesapi.exception.InvalidDeviceStateException;
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
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DeviceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DeviceService deviceService;

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DeviceController deviceController;

    private Device device;
    private Device device2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(deviceController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        device = new Device(1L, "Device1", "Brand1", Device.State.AVAILABLE, LocalDateTime.now());
        device2 = new Device(2L, "Device2", "Brand2", Device.State.IN_USE, LocalDateTime.now());
    }

    @Test
    void testCreateDevice() throws Exception {
        when(deviceService.createDevice(any(Device.class))).thenReturn(device);

        mockMvc.perform(post("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Device1\",\"brand\":\"Brand1\",\"state\":\"AVAILABLE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Device1"))
                .andExpect(jsonPath("$.brand").value("Brand1"));
    }

    @Test
    void testGetDeviceById() throws Exception {
        when(deviceService.getDeviceById(anyLong())).thenReturn(Optional.of(device));

        mockMvc.perform(get("/api/devices/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Device1"))
                .andExpect(jsonPath("$.brand").value("Brand1"));
    }

    @Test
    void testGetDeviceByIdNotFound() throws Exception {
        when(deviceService.getDeviceById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/devices/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(ResourceNotFoundException.class, result.getResolvedException()));
    }

    @Test
    void testGetAllDevices() throws Exception {
        when(deviceService.getAllDevices()).thenReturn(Arrays.asList(device));

        mockMvc.perform(get("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Device1"))
                .andExpect(jsonPath("$[0].brand").value("Brand1"));
    }

    @Test
    void testUpdateDevice() throws Exception {
        when(deviceService.getDeviceById(anyLong())).thenReturn(Optional.of(device));
        Device expectedDevice = new Device(1L, "UpdatedName", "Brand1", Device.State.AVAILABLE, device.getCreationTime());
        when(deviceService.updateDevice(anyLong(), any(Device.class))).thenReturn(expectedDevice);

        mockMvc.perform(patch("/api/devices/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"UpdatedName\",\"brand\":\"Brand1\",\"state\":\"AVAILABLE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UpdatedName"))
                .andExpect(jsonPath("$.brand").value("Brand1"));
    }

    @Test
    void testUpdateDeviceCreationTime() throws Exception {
        Device existingDevice = new Device(1L, "Device1", "Brand1", Device.State.AVAILABLE, LocalDateTime.now());
        when(deviceService.getDeviceById(anyLong())).thenReturn(Optional.of(existingDevice));
        when(deviceService.updateDevice(anyLong(), any(Device.class))).thenThrow(new InvalidDeviceStateException("Creation time cannot be updated"));

        mockMvc.perform(patch("/api/devices/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Device1\",\"brand\":\"Brand1\",\"state\":\"AVAILABLE\",\"creationTime\":\"" + LocalDateTime.now().plusDays(1) + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(InvalidDeviceStateException.class, result.getResolvedException()));
    }

    @Test
    void testUpdateDeviceInUse() throws Exception {
        Device existingDevice = new Device(1L, "Device1", "Brand1", Device.State.IN_USE, LocalDateTime.now());
        when(deviceService.getDeviceById(anyLong())).thenReturn(Optional.of(existingDevice));
        when(deviceService.updateDevice(anyLong(), any(Device.class))).thenThrow(new DeviceInUseException("Name and brand cannot be updated if the device is in use"));

        mockMvc.perform(patch("/api/devices/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Device1\",\"brand\":\"New Brand\",\"state\":\"IN_USE\",\"creationTime\":\"" + existingDevice.getCreationTime() + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(DeviceInUseException.class, result.getResolvedException()));
    }

    @Test
    void testDeleteDeviceInUse() throws Exception {
        when(deviceService.getDeviceById(anyLong())).thenReturn(Optional.of(device2));
        doThrow(new DeviceInUseException("Devices in use cannot be removed")).when(deviceService).deleteDevice(anyLong());

        mockMvc.perform(delete("/api/devices/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(DeviceInUseException.class, result.getResolvedException()));
    }
}
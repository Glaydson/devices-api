package com.glaydson.devicesapi.service;

import com.glaydson.devicesapi.dto.DeviceRequest;
import com.glaydson.devicesapi.dto.DeviceSearchCriteria;
import com.glaydson.devicesapi.exception.DeviceInUseException;
import com.glaydson.devicesapi.exception.MissingFieldsException;
import com.glaydson.devicesapi.exception.ResourceNotFoundException;
import com.glaydson.devicesapi.model.Device;
import com.glaydson.devicesapi.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DeviceService deviceService;

    private Device device1;
    private Device device2;
    private Device device3;
    private Device device4;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        device1 = new Device("Device1", "Brand1", Device.State.AVAILABLE);
        device2 = new Device(2L, "Device2", "Brand2", Device.State.IN_USE, LocalDateTime.now());
        device3 = new Device(3L, "Device3", "Brand3", Device.State.INACTIVE, LocalDateTime.now());
        device4 = new Device(4L, "Device4", "Brand4", Device.State.AVAILABLE, LocalDateTime.now());
    }

    @Test
    void testCreateDevice() {
        when(deviceRepository.save(any(Device.class))).thenReturn(device1);
        DeviceRequest deviceRequest = new DeviceRequest("Device1", "Brand1", Device.State.AVAILABLE);
        Device createdDevice = deviceService.createDevice(deviceRequest);
        assertNotNull(createdDevice);
        assertEquals(deviceRequest.getName(), createdDevice.getName());
        assertEquals(deviceRequest.getBrand(), createdDevice.getBrand());
        assertEquals(deviceRequest.getState(), createdDevice.getState());
    }

    @Test
    void testCreateDeviceWithNullState() {
        DeviceRequest deviceRequest = new DeviceRequest("Device1", "Brand1", null);
        assertThrows(MissingFieldsException.class, () -> deviceService.createDevice(deviceRequest));
    }

    @Test
    void testGetDeviceById() {
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(device1));
        Device foundDevice = deviceService.getDeviceById(1L);
        assertNotNull(foundDevice);
        assertEquals(device1.getName(), foundDevice.getName());
    }

    @Test
    void testGetDeviceByIdNotFound() {
        when(deviceRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> deviceService.getDeviceById(1L));
    }

    @Test
    void testSearchDevicesByBrandAndState() {
        when(deviceRepository.findByBrandAndState("Brand1", null)).thenReturn(List.of(device1));
        when(deviceRepository.findByBrandAndState("Brand2", Device.State.IN_USE)).thenReturn(List.of(device2));
        when(deviceRepository.findByBrand("Brand1")).thenReturn(List.of(device1));
        when(deviceRepository.findByState(Device.State.AVAILABLE)).thenReturn(List.of(device1, device4));
        List<Device> devices = deviceService.getDevicesByCriteria(new DeviceSearchCriteria("Brand1", null));
        assertEquals(1, devices.size());
        assertEquals("Brand1", devices.getFirst().getBrand());

        devices = deviceService.getDevicesByCriteria(new DeviceSearchCriteria("Brand2", Device.State.IN_USE));
        assertEquals(1, devices.size());
        assertEquals("Brand2", devices.getFirst().getBrand());

        devices = deviceService.getDevicesByCriteria(new DeviceSearchCriteria("Brand1", null));
        assertEquals(1, devices.size());
        assertEquals("Brand1", devices.getFirst().getBrand());

        devices = deviceService.getDevicesByCriteria(new DeviceSearchCriteria(null, Device.State.AVAILABLE));
        assertEquals(2, devices.size());

    }

    @Test
    void testUpdateDevice() {
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(device1));
        Device savedDevice = new Device(1L, "UpdatedName", device1.getBrand(), device1.getState(), device1.getCreationTime());
        when(deviceRepository.save(any(Device.class))).thenReturn(savedDevice);
        DeviceRequest preUpdated = new DeviceRequest("UpdatedName", device1.getBrand(), Device.State.IN_USE);
        Device updatedDevice = deviceService.updateDevice(1L, preUpdated);
        assertEquals("UpdatedName", updatedDevice.getName());
    }

    @Test
    void testUpdateDeviceInUse() {
        when(deviceRepository.findById(2L)).thenReturn(Optional.of(device2));
        DeviceRequest preUpdated = new DeviceRequest( "UpdatedName", device2.getBrand(), Device.State.IN_USE);
        assertThrows(DeviceInUseException.class, () -> deviceService.updateDevice(2L, preUpdated));
        device2 = deviceService.getDeviceById(2L);
        assertEquals("Device2", device2.getName());
    }

    @Test
    void testDeleteDevice() {
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(device1));
        doNothing().when(deviceRepository).deleteById(1L);
        deviceService.deleteDevice(1L);
        verify(deviceRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteDeviceInUse() {
        device1.setState(Device.State.IN_USE);
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(device1));
        assertThrows(DeviceInUseException.class, () -> deviceService.deleteDevice(1L));
    }

    @Test
    void testDeleteDeviceNotFound() {
        when(deviceRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> deviceService.deleteDevice(1L));
    }
}
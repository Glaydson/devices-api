package com.glaydson.devicesapi.repository;

import com.glaydson.devicesapi.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByBrand(String brand);
    List<Device> findByState(Device.State state);

    List<Device> findByBrandAndState(String brand, Device.State state);
}
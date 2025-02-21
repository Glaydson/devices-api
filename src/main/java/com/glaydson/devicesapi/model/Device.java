package com.glaydson.devicesapi.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import lombok.*;

@Entity
@Table(name = "devices")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "brand", nullable = false)
    private String brand;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private State state;

    @Column(name = "creation_time", updatable = false)
    private LocalDateTime creationTime;

    public Device(String name, String brand, State state) {
        this.name = name;
        this.brand = brand;
        this.state = state;
        this.creationTime = LocalDateTime.now();
    }

    public enum State {
        AVAILABLE,
        IN_USE,
        INACTIVE
    }
}
package com.example.ddmdemo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "road", nullable = false)
    private String road;

    @Column(name = "house_number", nullable = false)
    private String houseNumber;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "country")
    private String country;

    @Column(name = "lat")
    private Double lat;

    @Column(name = "lon")
    private Double lon;

    public Address(String road, String houseNumber, String city) {
        this.road = road;
        this.houseNumber = houseNumber;
        this.city = city;
    }
}

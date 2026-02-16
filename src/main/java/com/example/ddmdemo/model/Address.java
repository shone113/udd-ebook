package com.example.ddmdemo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
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

    @Column(name = "postcode", nullable = false)
    private String postcode;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "lat")
    private Double lat;

    @Column(name = "lon")
    private Double lon;
}

package com.example.ddmdemo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {
    private Long id;
    private String road;

    @JsonProperty("house-number")
    private String houseNumber;

    private String city;

    @JsonProperty("postalcode")
    private String postcode;

    private String country;

    private Double lat;
    private Double lon;

    public AddressDTO(String road, String houseNumber, String city) {
        this.road = road;
        this.houseNumber = houseNumber;
        this.city = city;
    }
}

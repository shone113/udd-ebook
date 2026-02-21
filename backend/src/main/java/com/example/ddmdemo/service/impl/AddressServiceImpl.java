package com.example.ddmdemo.service.impl;

import com.example.ddmdemo.dto.AddressDTO;
import com.example.ddmdemo.model.Address;
import com.example.ddmdemo.respository.AddressRepository;
import com.example.ddmdemo.service.interfaces.AddressService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    @Value("${opencage.api.key}")
    private String openCageApiKey;

    private static final String OPENCAGE_API_URL = "https://api.opencagedata.com/geocode/v1/json";

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    @Transactional
    public Address createAddress(AddressDTO addressDTO) {
        Address address = new Address(
                addressDTO.getRoad(),
                addressDTO.getHouseNumber(),
                addressDTO.getCity()
        );

        addressDTO.setCountry("Serbia");
        // Format address string for geocoding
        String addressString = formatAddressString(addressDTO);

        // Get lat/lon from OpenCage API
        try {
            double[] coordinates = geocodeAddress(addressString);
            if (coordinates != null) {
                address.setLat(coordinates[0]);
                address.setLon(coordinates[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Continue with null lat/lon if geocoding fails
        }

        // Save address first
        Address savedAddress = addressRepository.save(address);

        return savedAddress;
    }

    private String formatAddressString(AddressDTO addressDTO) {
        StringBuilder addressBuilder = new StringBuilder();

        if (addressDTO.getRoad() != null && !addressDTO.getRoad().isEmpty()) {
            addressBuilder.append(addressDTO.getRoad());
        }

        if (addressDTO.getHouseNumber() != null && !addressDTO.getHouseNumber().isEmpty()) {
            if (addressBuilder.length() > 0) {
                addressBuilder.append(" ");
            }
            addressBuilder.append(addressDTO.getHouseNumber());
        }

        if (addressDTO.getCity() != null && !addressDTO.getCity().isEmpty()) {
            if (addressBuilder.length() > 0) {
                addressBuilder.append(", ");
            }
            addressBuilder.append(addressDTO.getCity());
        }

        if (addressDTO.getPostcode() != null && !addressDTO.getPostcode().isEmpty()) {
            if (addressBuilder.length() > 0) {
                addressBuilder.append(", ");
            }
            addressBuilder.append(addressDTO.getPostcode());
        }

        if (addressDTO.getCountry() != null && !addressDTO.getCountry().isEmpty()) {
            if (addressBuilder.length() > 0) {
                addressBuilder.append(", ");
            }
            addressBuilder.append(addressDTO.getCountry());
        }

        return addressBuilder.toString();
    }

    private double[] geocodeAddress(String addressString) {
        try {
            URI uri = UriComponentsBuilder.fromHttpUrl(OPENCAGE_API_URL)
                    .queryParam("q", addressString)
                    .queryParam("key", openCageApiKey)
                    .build()
                    .toUri();

            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response.getBody());

                JsonNode results = jsonNode.get("results");
                if (results != null && results.isArray() && results.size() > 0) {
                    JsonNode firstResult = results.get(0);
                    JsonNode geometry = firstResult.get("geometry");

                    if (geometry != null) {
                        double lat = geometry.get("lat").asDouble();
                        double lon = geometry.get("lng").asDouble();
                        return new double[]{lat, lon};
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

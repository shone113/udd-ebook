package com.example.ddmdemo.service.interfaces;

import com.example.ddmdemo.dto.AddressDTO;
import com.example.ddmdemo.model.Address;
import org.springframework.stereotype.Service;

@Service
public interface AddressService {
    public Address createAddress(AddressDTO addressDTO);
 }

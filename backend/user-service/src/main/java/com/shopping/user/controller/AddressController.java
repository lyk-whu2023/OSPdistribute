package com.shopping.user.controller;

import com.shopping.user.dto.request.AddressRequest;
import com.shopping.user.dto.response.AddressResponse;
import com.shopping.user.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/user/{userId}")
    public List<AddressResponse> getUserAddresses(@PathVariable Long userId) {
        return addressService.getUserAddresses(userId);
    }

    @GetMapping("/{id}")
    public AddressResponse getAddress(@PathVariable Long id) {
        return addressService.getAddressById(id);
    }

    @PostMapping("/user/{userId}")
    public AddressResponse createAddress(@PathVariable Long userId, @RequestBody AddressRequest request) {
        return addressService.createAddress(userId, request);
    }

    @PutMapping("/{id}")
    public AddressResponse updateAddress(@PathVariable Long id, @RequestBody AddressRequest request) {
        return addressService.updateAddress(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
    }

    @PutMapping("/user/{userId}/default/{addressId}")
    public void setDefaultAddress(@PathVariable Long userId, @PathVariable Long addressId) {
        addressService.setDefaultAddress(userId, addressId);
    }
}
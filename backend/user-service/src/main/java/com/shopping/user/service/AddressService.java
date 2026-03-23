package com.shopping.user.service;

import com.shopping.user.dto.request.AddressRequest;
import com.shopping.user.dto.response.AddressResponse;
import java.util.List;

public interface AddressService {
    List<AddressResponse> getUserAddresses(Long userId);
    AddressResponse getAddressById(Long id);
    AddressResponse createAddress(Long userId, AddressRequest request);
    AddressResponse updateAddress(Long id, AddressRequest request);
    void deleteAddress(Long id);
    void setDefaultAddress(Long userId, Long addressId);
}
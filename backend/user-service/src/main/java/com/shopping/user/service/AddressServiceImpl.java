package com.shopping.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shopping.user.dto.request.AddressRequest;
import com.shopping.user.dto.response.AddressResponse;
import com.shopping.user.entity.Address;
import com.shopping.user.mapper.AddressMapper;
import com.shopping.user.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    
    private final AddressMapper addressMapper;
    private final OperationLogService operationLogService;

    @Override
    public List<AddressResponse> getUserAddresses(Long userId) {
        return addressMapper.selectList(new LambdaQueryWrapper<Address>()
                        .eq(Address::getUserId, userId)
                        .orderByDesc(Address::getIsDefault))
                .stream()// 将查询到的Address实体列表转换为Stream流，便于后续映射转换
                .map(this::convertToResponse)
                .collect(Collectors.toList());// 收集流结果为List集合
    }

    @Override
    public AddressResponse getAddressById(Long id) {
        Address address = addressMapper.selectById(id);
        return convertToResponse(address);
    }

    @Override
    @Transactional
    public AddressResponse createAddress(Long userId, AddressRequest request) {
        validateAddressRequest(request);
        
        Address address = new Address();
        BeanUtils.copyProperties(request, address);
        if (address.getIsDefault() == 1) {
            resetDefaultAddress(userId);
        }
        address.setUserId(userId);
        addressMapper.insert(address);
        
        log.info("地址创建成功：{}", address.getId());
        return convertToResponse(address);
    }

    @Override
    public AddressResponse updateAddress(Long id, AddressRequest request) {
        validateAddressRequest(request);
        
        Address existing = addressMapper.selectById(id);
        if (existing == null) {
            log.warn("地址不存在：{}", id);
            throw new RuntimeException("地址不存在");
        }
        Address address = new Address();
        BeanUtils.copyProperties(request, address);

        if (address.getIsDefault() == 1) {
            resetDefaultAddress(existing.getUserId());
        }

        address.setId(id);
        addressMapper.updateById(address);
        
        log.info("地址更新成功：{}", id);
        return convertToResponse(addressMapper.selectById(id));
    }

    @Override
    public void deleteAddress(Long id) {
        Address address = addressMapper.selectById(id);
        if (address == null) {
            log.warn("地址不存在：{}", id);
            throw new RuntimeException("地址不存在");
        }
        
        addressMapper.deleteById(id);
        log.info("地址删除成功：{}", id);
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long userId, Long addressId) {
        resetDefaultAddress(userId);

        Address address = addressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            log.warn("地址不存在或不属于该用户：{}", addressId);
            throw new RuntimeException("地址不存在");
        }

        address.setIsDefault(1);
        addressMapper.updateById(address);
        
        log.info("默认地址设置成功：{}", addressId);
    }

    private AddressResponse convertToResponse(Address address) {
        if (address == null) {
            return null;
        }
        AddressResponse response = new AddressResponse();
        BeanUtils.copyProperties(address, response);
        return response;
    }

    private void resetDefaultAddress(Long userId) {
        List<Address> addresses = addressMapper.selectList(new LambdaQueryWrapper<Address>()
                .eq(Address::getUserId, userId)
                .eq(Address::getIsDefault, 1));

        for (Address address : addresses) {
            address.setIsDefault(0);
            addressMapper.updateById(address);
        }
    }
    
    private void validateAddressRequest(AddressRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new RuntimeException("收货人不能为空");
        }
        if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
            throw new RuntimeException("手机号不能为空");
        }
        if (!ValidationUtil.isValidPhone(request.getPhone())) {
            throw new RuntimeException("手机号格式不正确");
        }
        if (request.getProvince() == null || request.getProvince().trim().isEmpty()) {
            throw new RuntimeException("省份不能为空");
        }
        if (request.getCity() == null || request.getCity().trim().isEmpty()) {
            throw new RuntimeException("城市不能为空");
        }
        if (request.getDetail() == null || request.getDetail().trim().isEmpty()) {
            throw new RuntimeException("详细地址不能为空");
        }
    }
}
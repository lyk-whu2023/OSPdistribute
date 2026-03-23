package com.shopping.user.dto.response;

import lombok.Data;

@Data
public class AddressResponse {
    private Long id;
    private String name;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String detail;
    private Integer isDefault;
}

package com.cadt.hrsolutionsbackend.payload;

import lombok.Data;

@Data
public class ProxyDto {

    private String name;
    private String email;

    // No password! The system generates it.
    // No username! We can generate one or use email.
}

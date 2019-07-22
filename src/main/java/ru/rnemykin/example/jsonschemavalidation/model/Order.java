package ru.rnemykin.example.jsonschemavalidation.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Data
public class Order {
    String id = UUID.randomUUID().toString();
    String status;
    String seller;
    String buyer;
    String buyerPhoneNumber;
    Set<Good> goods;
    BigDecimal totalSum;
    String deliveryAddress;
}

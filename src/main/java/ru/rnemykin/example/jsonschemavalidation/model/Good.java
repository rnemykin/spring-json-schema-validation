package ru.rnemykin.example.jsonschemavalidation.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Good {
    String name;
    Integer count;
    BigDecimal cost;
}

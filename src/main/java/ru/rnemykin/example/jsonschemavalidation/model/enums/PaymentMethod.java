package ru.rnemykin.example.jsonschemavalidation.model.enums;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

@Data
@Table
@Entity
public class PaymentMethod {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;
    private String type;


    public static PaymentMethod valueOf(@NotEmpty String type) {
        PaymentMethod method = new PaymentMethod();
        method.setType(type);
        return method;
    }
}

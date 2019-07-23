package ru.rnemykin.example.jsonschemavalidation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.rnemykin.example.jsonschemavalidation.model.PaymentMethod;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, String> {
}

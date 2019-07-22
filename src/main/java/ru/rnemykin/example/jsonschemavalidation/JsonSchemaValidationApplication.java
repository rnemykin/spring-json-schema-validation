package ru.rnemykin.example.jsonschemavalidation;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.rnemykin.example.jsonschemavalidation.model.enums.PaymentMethod;
import ru.rnemykin.example.jsonschemavalidation.repository.PaymentMethodRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@RestController
@SpringBootApplication
@RequiredArgsConstructor
public class JsonSchemaValidationApplication {
    private final JsonSchemaFactory jsonSchemaFactory;
    private final PaymentMethodRepository paymentMethodRepository;

    @Value("classpath:/jsonschema/Order.json")
    private Resource orderJsonSchemaResource;


    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        paymentMethodRepository.saveAll(
                Stream.of("Visa", "MasterCard", "MIR", "PayPal")
                        .map(PaymentMethod::valueOf)
                        .collect(Collectors.toList())
        );
    }


    @SneakyThrows
    @PostMapping("/orders/validate")
    public List<String> validate(@RequestBody JsonNode order) {
        JsonNode schema = JsonLoader.fromResource("/jsonschema/Order.json");
        ProcessingReport report = jsonSchemaFactory.getValidator().validate(schema, order, true);
        return StreamSupport.stream(report.spliterator(), false)
                .map(ProcessingMessage::asJson)
                .filter(m -> "validation".equals(m.get("domain").asText()))
                .map(m -> m.get("message").asText())
                .collect(Collectors.toList());
    }


    public static void main(String[] args) {
        SpringApplication.run(JsonSchemaValidationApplication.class, args);
    }

}

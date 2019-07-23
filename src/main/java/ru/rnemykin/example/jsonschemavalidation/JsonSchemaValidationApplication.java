package ru.rnemykin.example.jsonschemavalidation;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.helpers.TypeOnlySyntaxChecker;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.keyword.digest.helpers.SimpleDigester;
import com.github.fge.jsonschema.keyword.validator.KeywordValidator;
import com.github.fge.jsonschema.library.DraftV4Library;
import com.github.fge.jsonschema.library.Keyword;
import com.github.fge.jsonschema.library.Library;
import com.github.fge.jsonschema.library.LibraryBuilder;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.rnemykin.example.jsonschemavalidation.model.PaymentMethod;
import ru.rnemykin.example.jsonschemavalidation.repository.PaymentMethodRepository;
import ru.rnemykin.spring.boot.jsonschema.keyword.KeywordProcessor;
import ru.rnemykin.spring.boot.jsonschema.support.SpringAwareKeywordValidatorFactory;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.github.fge.jackson.NodeType.STRING;

@RestController
@SpringBootApplication
@RequiredArgsConstructor
public class JsonSchemaValidationApplication {
    @Autowired
    private JsonSchemaFactory jsonSchemaFactory;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;


    @Bean
    public Library library(KeywordProcessor processor, ApplicationContext ctx) {
        LibraryBuilder builder = DraftV4Library.get().thaw();
        builder.addKeyword(
                Keyword.newBuilder(processor.keyword())
                        .withSyntaxChecker(new TypeOnlySyntaxChecker(processor.keyword(), STRING))
                        .withDigester(new SimpleDigester(processor.keyword(), STRING))
                        .withValidatorFactory(new SpringAwareKeywordValidatorFactory(ctx, ((KeywordValidator) processor).getClass()))
                        .freeze()
        );
        return builder.freeze();
    }

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
    public Map<String, String> validate(@RequestBody JsonNode order) {
        JsonNode schema = JsonLoader.fromResource("/jsonschema/Order.json");
        ProcessingReport report = jsonSchemaFactory.getValidator().validate(schema, order, true);
        return StreamSupport.stream(report.spliterator(), false)
                .map(ProcessingMessage::asJson)
                .filter(m -> "validation".equals(m.get("domain").asText()))
                .collect(Collectors.toMap(m -> m.get("instance").get("pointer").asText(), m -> m.get("message").asText()));
    }


    public static void main(String[] args) {
        SpringApplication.run(JsonSchemaValidationApplication.class, args);
    }

}

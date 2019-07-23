package ru.rnemykin.example.jsonschemavalidation.keyword;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.processing.Processor;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.keyword.validator.AbstractKeywordValidator;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.msgsimple.bundle.MessageBundle;
import lombok.ToString;
import org.springframework.stereotype.Component;
import ru.rnemykin.example.jsonschemavalidation.model.PaymentMethod;
import ru.rnemykin.example.jsonschemavalidation.repository.PaymentMethodRepository;
import ru.rnemykin.spring.boot.jsonschema.keyword.KeywordProcessor;

import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@ToString
@Component
public class EnumDbValueKeywordProcessor extends AbstractKeywordValidator implements KeywordProcessor {
    private static final String KEYWORD = "enumDbValue";
    private final PaymentMethodRepository paymentMethodRepository;

    protected EnumDbValueKeywordProcessor(PaymentMethodRepository paymentMethodRepository) {
        super(KEYWORD);
        this.paymentMethodRepository = paymentMethodRepository;
    }

    @Override
    public String keyword() {
        return KEYWORD;
    }


    @Override
    public void validate(Processor<FullData, FullData> processor, ProcessingReport report, MessageBundle bundle, FullData data) throws ProcessingException {
        String paymentMethod = data.getInstance().getNode().asText();
        List<String> methods = paymentMethodRepository.findAll().stream().map(PaymentMethod::getType).collect(toList());
        if(methods.stream().noneMatch(m -> Objects.equals(m, paymentMethod))) {
           report.error(newMsg(data, bundle, "allowed only " + methods + " methods"));
        }
    }
}

package ru.rnemykin.example.jsonschemavalidation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StreamUtils;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class JsonSchemaValidationApplicationTests {
    private static final TypeReference<Map<String, String>> TYPE_REF = new TypeReference<Map<String, String>>() {};

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;


    @Test
    public void orderFailValidation() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                post("/orders/validate")
                        .content(StreamUtils.copyToByteArray(this.getClass().getResourceAsStream("/order-bad.json")))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn().getResponse();

        Map<String, String> errorMessages = mapper.readValue(response.getContentAsByteArray(), TYPE_REF);
        Assert.assertFalse("must contains elements", errorMessages.isEmpty());
        errorMessages.forEach((k,v) -> System.out.println(k + ": " + v));
    }

    @Test
    public void orderOkValidation() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                post("/orders/validate")
                        .content(StreamUtils.copyToByteArray(this.getClass().getResourceAsStream("/order-ok.json")))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn().getResponse();

        Map<String, String> errorMessages = mapper.readValue(response.getContentAsByteArray(), TYPE_REF);
        Assert.assertTrue("must not contains elements", errorMessages.isEmpty());
    }

}

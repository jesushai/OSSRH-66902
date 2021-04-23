package com.lemon.schemaql.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lemon.schemaql.engine.parser.input.InputArgument;
import com.lemon.schemaql.engine.parser.input.MutationInput;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/9/1
 */
public class JsonReadTest {

    @Test
    public void test1() throws JsonProcessingException {
        String json = "{\"target\":\"TTT\",\"type\":\"mutation\",\"mutationType\":\"create\",\"input\":{\"id\":\"12345\",\"body\":\"12341234\"},\"result\":{}}";
//        String json = "{\"target\":\"TTT\",\"type\":\"mutation\",\"mutationType\":\"create\",\"input\":\"333\",\"result\":{}}";
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        MutationInput input = objectMapper.readValue(json, MutationInput.class);
        System.out.println(input);
    }
}

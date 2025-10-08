package org.telegram.antischool.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.antischool.dto.SendRequestModel;
import org.telegram.antischool.dto.WordItem;

import java.util.Arrays;
import java.util.List;

public class Converter {
    String nodeName;
    boolean isResultArray;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private Converter(String nodeName, boolean isResultArray) {
        this.nodeName = nodeName;
        this.isResultArray = isResultArray;
    }


    public List<WordItem> convert(String payload) {
        try {
            JsonNode parent = new ObjectMapper().readTree(String.valueOf(payload));
            String data = parent.findValue(nodeName).toString();
            ObjectMapper mapper = new ObjectMapper();
            if (isResultArray) {
                WordItem[] array = mapper.readValue(data, WordItem[].class);
                return Arrays.stream(array).toList();
            } else {
                WordItem wordItem = mapper.readValue(data, WordItem.class);
                return List.of(wordItem);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException();
        }
    }

    public static String convertMsg(SendRequestModel message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException();
        }
    }

    public static Converter ArrayConverter() {
        return new Converter("Items", true);
    }

    public static Converter DataConverter() {
        return new Converter("Data", false);
    }

    public static Converter ArrayDataConverter() {
        return new Converter("Data", true);
    }

    public static Converter ValueConverter() {
        return new Converter("Value", false);
    }
}
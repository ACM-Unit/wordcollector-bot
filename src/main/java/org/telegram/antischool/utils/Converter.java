package org.telegram.antischool.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.antischool.dto.SendRequestModel;
import org.telegram.antischool.dto.WordItem;
import org.telegram.antischool.model.Word;

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

    public static Word toEntity(WordItem item) {
        if (item == null) {
            return null;
        }

        Word word = new Word();
        word.setId(0); // optional, if generated or managed separately
        word.setWordId(item.getId());
        word.setWord(item.getWord());
        word.setTranslation(item.getTranslation());
        word.setPupilMaterialsId(item.getPupilMaterialsId());
        word.setDateCreate(item.getDateCreate());
        word.setAudio(item.getAudio());
        word.setFileId(item.getFileId());
        word.setStatus(item.getStatus());
        word.setLearnedWords(item.getLearnedWords());
        word.setWordsToStudied(item.getWordsToStudied());
        return word;
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
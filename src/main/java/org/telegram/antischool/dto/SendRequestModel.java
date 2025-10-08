package org.telegram.antischool.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SendRequestModel {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private String controller;
    private String method;
    private String projectName;
    private Value value;


    public String getValue() throws JsonProcessingException {
        return objectMapper.writeValueAsString(value);
    }

    public interface Value {}


    public record WordsValue(
        int PupilId,
        String SearchTerm,
        int Take,
        boolean IsRandom) implements Value { }

    public record StatValue(
            int PupilId,
            String Search,
            int GroupId,
            int PupilIdInGroup,
            boolean IsTeacher) implements Value { }

    public record ChangeStatusValue(
            Integer[] WordsId,
            int Status) implements Value { }

    public record ValueForDelete(Integer[] WordsId) implements Value { }

    public record TranslateWordValue(
            String Text,
            String CurrentLang,
            int CurrentLangId,
            String TargetLang,
            int TargetLangId,
            boolean WithAudio,
            int CurrentUserId) implements Value { }

    public record SaveWordValue(
            int WordId,
            int LangId,
            int FileId,
            String FileUrl,
            int PupilId,
            String Translation,
            String Word) implements Value { }


}

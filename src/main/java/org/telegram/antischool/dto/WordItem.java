package org.telegram.antischool.dto;


import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class WordItem {

    @JsonAlias("Id")
    private int id;
    @JsonAlias({"Word", "SourceText"})
    private String word;
    @JsonAlias("Translation")
    private String translation;
    @JsonAlias("PupilMaterialsId")
    private String pupilMaterialsId;
    @JsonAlias("DateCreate")
    private String dateCreate;
    @JsonAlias("Audio")
    private String audio;
    @JsonAlias({"FileId", "AudioId"})
    private String fileId;
    @JsonAlias("Status")
    private String status;
    @JsonAlias("LearnedWords")
    private int learnedWords;
    @JsonAlias("WordsToStudied")
    private int wordsToStudied;

    @JsonProperty("Translates")
    private void unpackTranslates(List<Map<String, Object>> translates) {
        if (translates != null && !translates.isEmpty()) {
            this.translation = (String) translates.get(0).get("Text");
        }
    }

    @Override
    public String toString() {
        return "WordItem{" +
                "id=" + id +
                ", word='" + word + '\'' +
                ", translation='" + translation + '\'' +
                ", pupilMaterialsId='" + pupilMaterialsId + '\'' +
                ", dateCreate='" + dateCreate + '\'' +
                ", audio='" + audio + '\'' +
                ", fileId='" + fileId + '\'' +
                ", status='" + status + '\'' +
                ", learnedWords=" + learnedWords +
                ", wordsToStudied=" + wordsToStudied +
                '}';
    }
}


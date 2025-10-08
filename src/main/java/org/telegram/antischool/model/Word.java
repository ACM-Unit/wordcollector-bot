package org.telegram.antischool.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table
public class Word {
    @Id
    private int id;
    private int wordId;
    private String word;
    private String translation;
    private String pupilMaterialsId;
    private String dateCreate;
    private String audio;
    private String fileId;
    private String status;
    private int learnedWords;
    private int wordsToStudied;
}

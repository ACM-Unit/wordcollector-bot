package org.telegram.antischool.services;

import org.telegram.antischool.dto.WordItem;

import java.util.List;
import java.util.function.Consumer;

public interface WordService {
    void getToLearnWords(int count, Consumer<List<WordItem>> callback);
    void getLearnedWords(int count, Consumer<List<WordItem>> callback);
    void getPupilStatistic(Consumer<List<WordItem>> callback);
    void changeWordStatus(List<WordItem> words, int status, Consumer<List<WordItem>> callback);
    void deleteWords(List<WordItem> words, Consumer<List<WordItem>> callback);
    void translateWord(String word, Consumer<List<WordItem>> callback);
    void saveWord(WordItem word);
}

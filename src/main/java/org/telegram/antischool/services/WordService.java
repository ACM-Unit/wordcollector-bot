package org.telegram.antischool.services;

import org.telegram.antischool.dto.WordItem;
import org.telegram.antischool.model.Word;
import reactor.core.publisher.Flux;

import java.util.List;

public interface WordService {
    Flux<List<WordItem>> getToLearnWords(int count);
    Flux<List<WordItem>> getLearnedWords(int count);
    Flux<List<WordItem>> getPupilStatistic();
    Flux<List<WordItem>> changeWordStatus(List<WordItem> words, int status);
    Flux<List<WordItem>> deleteWords(List<WordItem> words);
    Flux<List<WordItem>> translateWord(String word);
    Flux<Word> saveWord(List<Word> word);
}

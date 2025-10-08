package org.telegram.antischool.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.telegram.antischool.dto.WordItem;
import org.telegram.antischool.repositories.WordSocketRepository;
import org.telegram.antischool.services.WordService;

import java.util.List;
import java.util.function.Consumer;

@Service
public class WordServiceImpl implements WordService {

    @Autowired
    WordSocketRepository repository;

    @Override
    @Cacheable("toLearn")
    public void getToLearnWords(int count, Consumer<List<WordItem>> callback) {
        repository.getArrayMessage(Messages.getToLearnVocabularyMessage(count), callback);

    }

    @Override
    @Cacheable("learned")
    public void getLearnedWords(int count, Consumer<List<WordItem>> callback) {
        repository.getArrayMessage(
                Messages.getLearnedVocabularyMessage(
                        count), callback);
    }

    @Override
    @Cacheable("stat")
    public void getPupilStatistic(Consumer<List<WordItem>> callback) {
        repository.getDataMessage(Messages.getPupilStatMessage(), callback);
    }

    @Override
    public void changeWordStatus(List<WordItem> words, int status, Consumer<List<WordItem>> callback) {
        Integer[] wordsArray = words.stream().map(WordItem::getId).toList().toArray(new Integer[0]);
        repository.getArrayDataMessage(Messages.getChangeStatusMessage(wordsArray, status), callback);
    }

    @Override
    public void deleteWords(List<WordItem> words, Consumer<List<WordItem>> callback) {
        Integer[] wordsArray = words.stream().map(WordItem::getId).toList().toArray(new Integer[0]);
        repository.getArrayDataMessage(Messages.deleteWordMessage(wordsArray), callback);
    }

    @Override
    public void translateWord(String word, Consumer<List<WordItem>> callback) {
        repository.getDataMessage(Messages.translateWordMessage(word), m -> repository.getValueMessage(Messages.addWordMessage(m.get(0)), callback));
    }

    @Override
    public void saveWord(WordItem word) {

    }

}

package org.telegram.antischool.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import org.telegram.antischool.dto.WordItem;
import org.telegram.antischool.model.Word;
import org.telegram.antischool.repositories.WordRepository;
import org.telegram.antischool.repositories.WordSocketRepository;
import org.telegram.antischool.services.WordService;
import org.telegram.antischool.utils.Converter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@EnableCaching
public class WordServiceImpl implements WordService {

    @Autowired
    WordSocketRepository repository;

    @Autowired
    WordRepository dbRepository;

    @Override
    public Flux<List<WordItem>> getToLearnWords(int count) {
        return repository.getArrayMessage(Messages.getToLearnVocabularyMessage(count))
                .flatMap(words ->
                        saveWord(words.stream()
                                        .map(Converter::toEntity)
                                        .toList())
                                .then(Mono.just(words))
                );
    }

    @Override
    @Cacheable("learned")
    public Flux<List<WordItem>> getLearnedWords(int count) {
        return repository.getArrayMessage(Messages.getLearnedVocabularyMessage(count));
    }

    @Override
    @Cacheable("stat")
    public Flux<List<WordItem>> getPupilStatistic() {
        return repository.getDataMessage(Messages.getPupilStatMessage());
    }

    @Override
    public Flux<List<WordItem>> changeWordStatus(List<WordItem> words, int status) {
        Integer[] wordsArray = words.stream().map(WordItem::getId).toList().toArray(new Integer[0]);
        return repository.getArrayDataMessage(Messages.getChangeStatusMessage(wordsArray, status));
    }

    @Override
    public Flux<List<WordItem>> deleteWords(List<WordItem> words) {
        Integer[] wordsArray = words.stream().map(WordItem::getId).toList().toArray(new Integer[0]);
        return repository.getArrayDataMessage(Messages.deleteWordMessage(wordsArray));
    }

    @Override
    public Flux<List<WordItem>> translateWord(String word) {
        return repository.getDataMessage(Messages.translateWordMessage(word))
                .flatMap(m ->
                        repository.getValueMessage(Messages.addWordMessage(m.getFirst()))
                                .thenMany(saveWord(m.stream().map(Converter::toEntity).toList()))
                                .then(Mono.just(m))
                );
    }

    @Override
    public Flux<Word> saveWord(List<Word> words) {
        return dbRepository.saveAll(words);
    }

}

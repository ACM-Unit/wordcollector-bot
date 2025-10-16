package org.telegram.antischool.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.telegram.antischool.model.Word;

@Repository
public interface WordRepository extends ReactiveCrudRepository<Word, Long> {
}

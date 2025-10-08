package org.telegram.antischool.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.telegram.antischool.model.TransactionBackUp;

@Repository
public interface TransactionBackUpRepository extends ReactiveCrudRepository<TransactionBackUp, Long> {
}

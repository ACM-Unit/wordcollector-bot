package org.telegram.antischool.services;

import org.telegram.antischool.dto.TransactionWithUser;
import org.telegram.antischool.model.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionService {
    Mono<Transaction> createTransaction(Transaction transaction);
    Mono<Transaction> updateTransaction(Transaction transaction);

    Flux<Transaction> findTop20OrderByCreatedAtDesc();

    Flux<TransactionWithUser> findTop20WithUsers();
    Flux<Void> clearAll();
}

package org.telegram.antischool.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.antischool.dto.TransactionWithUser;
import org.telegram.antischool.model.Transaction;
import org.telegram.antischool.model.TransactionBackUp;
import org.telegram.antischool.repositories.TransactionBackUpRepository;
import org.telegram.antischool.repositories.TransactionRepository;
import org.telegram.antischool.repositories.UserRepository;
import org.telegram.antischool.services.TransactionService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private TransactionBackUpRepository transactionBackUpRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public Mono<Transaction> createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public Mono<Transaction> updateTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public Flux<Transaction> findTop20OrderByCreatedAtDesc() {
        return transactionRepository.findTop20OrderByCreatedAtDesc();
    }

    public Flux<TransactionWithUser> findTop20WithUsers() {
        return transactionRepository.findTop20OrderByCreatedAtDesc()
                .concatMap(tx -> userRepository.findById(tx.getUserId())
                        .map(user -> new TransactionWithUser(tx, user))
                );
    }

    @Transactional
    public Flux<Void> clearAll() {
        return transactionRepository.findAll().map(t -> new TransactionBackUp(
                t.getUserId(),
                t.getType(),
                t.getDescription(),
                t.getAmount()
        )).as(transactionBackUpRepository::saveAll).thenMany(transactionRepository.deleteAll());
    }
}

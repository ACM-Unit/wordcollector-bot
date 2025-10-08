package org.telegram.antischool.repositories;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.telegram.antischool.model.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends ReactiveCrudRepository<Transaction, Long> {

    @Query("SELECT * FROM transactions WHERE user_id = :userId ORDER BY created_at DESC LIMIT 10")
    Flux<Transaction> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT * FROM transactions ORDER BY created_at DESC LIMIT 20")
    Flux<Transaction> findTop20OrderByCreatedAtDesc();

    @Query("SELECT * FROM transactions WHERE user_id = :userId ORDER BY created_at DESC")
    Flux<Transaction> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY created_at DESC LIMIT 50")
    Flux<Transaction> findByTypeOrderByCreatedAtDesc(String type);

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = :userId AND type = 'EARN'")
    Flux<Integer> getTotalEarnedByUserId(Long userId);

    @Query("INSERT INTO transactions_bckup (id, type, amount, created_at) VALUES (:id, :type, :amount, :createdAt)")
    Mono<Integer> backUpTransaction(Long id, String type, BigDecimal amount, LocalDateTime createdAt);

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = :userId AND type = 'SPEND'")
    Flux<Integer> getTotalSpentByUserId(Long userId);
}

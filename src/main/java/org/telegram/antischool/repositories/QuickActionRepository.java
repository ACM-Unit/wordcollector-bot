package org.telegram.antischool.repositories;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.telegram.antischool.model.QuickAction;
import org.telegram.antischool.model.Type;
import reactor.core.publisher.Flux;

@Repository
public interface QuickActionRepository extends ReactiveCrudRepository<QuickAction, Long> {
    @Query("SELECT * FROM quick_actions WHERE type = :type")
    Flux<QuickAction> findAllByType(Type type);
    @Query("SELECT * FROM quick_actions WHERE user_id = :userId AND type = :type")
    Flux<QuickAction> findAllByUserIdAndType(long userId, Type type);
}

package org.telegram.antischool.repositories;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.telegram.antischool.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {
    Mono<User> findByName(String name);
    @Query("SELECT * FROM users ORDER BY house_points DESC")
    Flux<User> findAllOrderByHousePoints();
}

package org.telegram.antischool.services;

import org.telegram.antischool.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<User> addUser(User user);

    Mono<User> getUser(String name);

    Flux<User> getUsers();

    Mono<User> addHousePoint(long userId, int housePoints);

    Mono<User> saveUser(User user);

    Flux<User> getLeaderboard();

    Mono<User> getUser(Long id);

    Mono<User> removeHousePoint(Long id, Integer points);
}

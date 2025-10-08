package org.telegram.antischool.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.antischool.model.User;
import org.telegram.antischool.repositories.UserRepository;
import org.telegram.antischool.services.UserService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    public UserRepository userRepository;

    public Mono<User> addUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public Mono<User> getUser(String name) {
        return userRepository.findByName(name);
    }

    @Override
    public Flux<User> getUsers() {
        return userRepository.findAll();
    }


    @Transactional
    public Mono<User> addHousePoint(long userId, int housePoints) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
                .flatMap(user -> {
                    user.setHousePoints(user.getHousePoints() + housePoints);
                    return userRepository.save(user);
                });
    }

    @Transactional
    public Mono<User> removeHousePoint(Long userId, Integer housePoints) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
                .flatMap(user -> {
                    if (user.getHousePoints() < housePoints) {
                        return Mono.just(user);
                    }
                    user.setHousePoints(user.getHousePoints() - housePoints);
                    return userRepository.save(user);
                });
    }

    public Flux<User> getLeaderboard() {
        return userRepository.findAllOrderByHousePoints();
    }

    @Override
    public Mono<User> getUser(Long id) {
        return userRepository.findById(id);
    }


    @Override
    public Mono<User> saveUser(User user) {
        return userRepository.save(user);
    }
}

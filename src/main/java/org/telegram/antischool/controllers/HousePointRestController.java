package org.telegram.antischool.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.telegram.antischool.model.TransactionBackUp;
import org.telegram.antischool.model.User;
import org.telegram.antischool.services.TransactionService;
import org.telegram.antischool.services.UserService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class HousePointRestController {

    @Autowired
    public UserService userService;
    @Autowired
    private TransactionService transactionService;

    @GetMapping("/user")
    public Mono<User> getUserByName(@RequestParam String name) {
        return userService.getUser(name);
    }

    @GetMapping("/users")
    public Flux<User> getAllUsers() {
        return userService.getUsers();
    }

    @PostMapping("/user/create")
    public Mono<User> createUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @PostMapping("/user/{id}/add-points")
    public Mono<User> addHousePoints(@PathVariable Long id, @RequestParam int points) {
        return userService.addHousePoint(id, points);
    }

    @GetMapping(path = "/api/transaction/clean-up")
    public Flux<Void> spendPoints() {
        return transactionService.clearAll();
    }
}

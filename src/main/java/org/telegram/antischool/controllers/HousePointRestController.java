package org.telegram.antischool.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.telegram.antischool.model.Transaction;
import org.telegram.antischool.model.User;
import org.telegram.antischool.services.TransactionService;
import org.telegram.antischool.services.UserService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

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

    @PostMapping("/api/dump")
    public Flux<Map<String, Object>> getFullDump() {
        Flux<User> usersMono = userService.getUsers(); // assuming it returns Mono<List<User>>
        Flux<Transaction> transactionsMono = transactionService.getAllTransactions(); // Mono<List<Transaction>>

        return Flux.zip(usersMono, transactionsMono)
                .map(tuple -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("users", tuple.getT1());
                    result.put("transactions", tuple.getT2());
                    return result;
                });
    }
}

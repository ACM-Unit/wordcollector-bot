package org.telegram.antischool.controllers;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.telegram.antischool.model.*;
import org.telegram.antischool.services.QuickActionService;
import org.telegram.antischool.services.TransactionService;
import org.telegram.antischool.services.UserService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class HousePointController {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private QuickActionService quickActionService;

    @GetMapping("/api/leaderboard")
    public Mono<String> getLeaderboard(Model model) {
        model.addAttribute("users", userService.getLeaderboard());
        return Mono.just("fragments/leaderboard :: leaderboard-table");
    }

    @Data
    public static class HousePointsRequest {
        private Long id;
        private Integer points;
        private String description;
    }

    @PostMapping(path = "/api/earn-points")
    public Mono<String> earnPoints(@RequestBody HousePointsRequest request, Model model) {
        System.out.println("Earning points - User: " + request.getId() +
                ", Points: " + request.getPoints());
        return userService.addHousePoint(request.getId(), request.getPoints())
                .then(transactionService.createTransaction(
                        new Transaction(request.getId(), Type.EARN, request.getDescription(), request.getPoints())
                ))
                .then(userService.getLeaderboard()
                        .collectList()
                        .doOnNext(users -> model.addAttribute("users", users)))
                .thenReturn("fragments/leaderboard :: leaderboard-table");
    }

    @GetMapping(path = "/api/quick-earn-points")
    public Mono<String> earnPoints(@RequestParam Long id,
                                   @RequestParam Integer points,
                                   @RequestParam String description,
                                   Model model) {
        return userService.addHousePoint(id, points)
                .then(transactionService.createTransaction(
                        new Transaction(id, Type.EARN, description, points)
                ))
                .then(userService.getLeaderboard()
                        .collectList()
                        .doOnNext(users -> model.addAttribute("users", users)))
                .thenReturn("fragments/leaderboard :: leaderboard-table");
    }


    // Get quick action buttons
    @GetMapping("/api/quick-actions")
    public Mono<String> getQuickActions(Model model) {
        return userService.getUser("Dima")
                .flatMap(user ->
                        Mono.zip(
                                quickActionService.getQuickActions(user.getId(), Type.EARN).collectList(),
                                quickActionService.getQuickActions(user.getId(), Type.SPEND).collectList()
                        ).map(tuple -> {
                            var earnActions = tuple.getT1();
                            var spendActions = tuple.getT2();
                            model.addAttribute("user", user);
                            model.addAttribute("earnActions", earnActions);
                            model.addAttribute("spendActions", spendActions);
                            return "fragments/quick-actions :: quick-actions";
                        })
                );
    }

    @GetMapping("/api/remove-quick-action")
    public Mono<String> removeQuickActions(@RequestParam long userId, @RequestParam long id,  Model model) {
        return quickActionService.removeQuickAction(id)
                .then(quickActionService.getQuickActions(userId, Type.EARN)
                        .collectList()
                        .doOnNext(actions -> model.addAttribute("actions", actions)))
                .then(userService.getUser("Dima").doOnNext(user -> model.addAttribute("user", user)))
                .thenReturn("fragments/quick-actions :: quick-actions");
    }

    @GetMapping("/house-points")
    public Mono<String> listUsers(Model model) {
        return userService.getLeaderboard()
                .collectList()
                .doOnNext(users -> {
                    model.addAttribute("users", users);
                    String userNames = users.stream()
                            .map(User::getName)
                            .collect(Collectors.joining(", "));
                    model.addAttribute("userNames", userNames);
                })
                .thenReturn("index");
    }

    @GetMapping("/ui/users/{id}")
    public Mono<String> userDetail(@PathVariable Long id, Model model) {
        return userService.getUser(id)
                .doOnNext(user -> model.addAttribute("user", user))
                .thenReturn("user-detail");
    }

    @GetMapping("/api/transactions")
    public Mono<String> getTransactions(Model model) {
        model.addAttribute("transactions", transactionService.findTop20WithUsers());
        return Mono.just("fragments/transactions :: transactions-list");
    }

    @PostMapping("/api/spend-points")
    @ResponseBody
    public Mono<String> spendPoints(@RequestBody HousePointsRequest request,
                                    Model model) {
        return userService.removeHousePoint(request.getId(), request.getPoints())
                .then(transactionService.createTransaction(
                                        new Transaction(request.getId(), Type.SPEND, request.description, request.getPoints())
                                ))
                .then(userService.getLeaderboard()
                        .collectList()
                        .doOnNext(users -> model.addAttribute("users", users)))
            .thenReturn("fragments/leaderboard :: leaderboard-table");
    }

    @GetMapping(path = "/api/quick-spend-points")
    public Mono<String> spendPoints(@RequestParam Long id,
                                   @RequestParam Integer points,
                                   @RequestParam String description,
                                   Model model) {
        return userService.removeHousePoint(id, points)
                .then(transactionService.createTransaction(
                        new Transaction(id, Type.SPEND, description, points)
                ))
                .then(userService.getLeaderboard()
                        .collectList()
                        .doOnNext(users -> model.addAttribute("users", users)))
                .thenReturn("fragments/leaderboard :: leaderboard-table");
    }

    @PostMapping(path = "/api/add-quick-earn-action")
    public Mono<String> quickAction(@RequestBody HousePointsRequest request,
                                    Model model) {
        log.info("Add quick-earn-action {}, {}, {}", request.getId(), request.getDescription(), request.getPoints());
        return quickActionService.addQuickAction(new QuickAction(request.getId(), request.getDescription(), request.getPoints(), Type.EARN))
                .flatMap(user ->
                        Mono.zip(
                                quickActionService.getQuickActions(user.getId(), Type.EARN).collectList(),
                                quickActionService.getQuickActions(user.getId(), Type.SPEND).collectList()
                        ).map(tuple -> {
                            var earnActions = tuple.getT1();
                            var spendActions = tuple.getT2();
                            model.addAttribute("user", user);
                            model.addAttribute("earnActions", earnActions);
                            model.addAttribute("spendActions", spendActions);
                            return "fragments/quick-actions :: quick-actions";
                        })
                );
    }

    @PostMapping(path = "/api/add-quick-spend-action")
    public Mono<String> quickSpendAction(@RequestBody HousePointsRequest request,
                                    Model model) {
        return quickActionService.addQuickAction(new QuickAction(request.getId(), request.getDescription(), request.getPoints(), Type.SPEND))
                .flatMap(user ->
                        Mono.zip(
                                quickActionService.getQuickActions(user.getId(), Type.EARN).collectList(),
                                quickActionService.getQuickActions(user.getId(), Type.SPEND).collectList()
                        ).map(tuple -> {
                            var earnActions = tuple.getT1();
                            var spendActions = tuple.getT2();
                            model.addAttribute("user", user);
                            model.addAttribute("earnActions", earnActions);
                            model.addAttribute("spendActions", spendActions);
                            return "fragments/quick-actions :: quick-actions";
                        })
                );
    }
}

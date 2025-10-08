package org.telegram.antischool.services;

import org.telegram.antischool.model.QuickAction;
import org.telegram.antischool.model.Type;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface QuickActionService {

    Mono<QuickAction> addQuickAction(QuickAction quickAction);
    Flux<QuickAction> getQuickActions();
    Flux<QuickAction> getQuickActions(Type type);
    Flux<QuickAction> getQuickActions(long userId, Type type);

    Mono<Void> removeQuickAction(long id);
}

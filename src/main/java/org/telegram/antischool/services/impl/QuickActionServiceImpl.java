package org.telegram.antischool.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.antischool.model.QuickAction;
import org.telegram.antischool.model.Type;
import org.telegram.antischool.repositories.QuickActionRepository;
import org.telegram.antischool.services.QuickActionService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class QuickActionServiceImpl implements QuickActionService {

    @Autowired
    public QuickActionRepository quickActionRepository;
    @Override
    public Mono<QuickAction> addQuickAction(QuickAction quickAction) {
        return quickActionRepository.save(quickAction);
    }

    @Override
    public Flux<QuickAction> getQuickActions() {
        return quickActionRepository.findAll();
    }

    @Override
    public Flux<QuickAction> getQuickActions(Type type) {
        return quickActionRepository.findAllByType(type);
    }

    @Override
    public Flux<QuickAction> getQuickActions(long userId, Type type) {
        return quickActionRepository.findAllByUserIdAndType(userId, type);
    }

    @Override
    public Mono<Void> removeQuickAction(long id) {
        return quickActionRepository.deleteById(id);
    }
}

package com.ing.brokerage.common;

import org.springframework.context.ApplicationEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class AggregateRoot<T> {
    private T id;
    private Long version;

    private List<ApplicationEvent> domainEvents = new ArrayList<>();
    protected AggregateRoot(T id) {
        this.id = id;
    }
    public List<ApplicationEvent> getDomainEvents() {
        return domainEvents;
    }
    protected void registerEvent(ApplicationEvent event) {
        domainEvents.add(event);
    }
    public void clearDomainEvents() {
        domainEvents.clear();
    }
    public T getId() {
        return id;
    }

    public void updateVersion(Long version) {
        this.version = version;
    }

    public Long version() {
        return version;
    }
}

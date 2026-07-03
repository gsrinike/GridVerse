package eu.gridverse.infra.event;

public interface EventPublisherService { void publish(String topic, String key, Object event); }

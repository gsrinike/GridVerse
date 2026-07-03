package eu.gridverse.infra.event.rabbitmq;
import eu.gridverse.infra.event.EventPublisherService; import org.springframework.amqp.rabbit.core.RabbitTemplate;
public final class RabbitMqEventPublisher implements EventPublisherService { private final RabbitTemplate rabbit; public RabbitMqEventPublisher(RabbitTemplate rabbit){this.rabbit=rabbit;} public void publish(String topic,String key,Object event){rabbit.convertAndSend(topic,key,event);} }

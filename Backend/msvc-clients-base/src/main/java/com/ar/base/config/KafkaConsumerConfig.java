package com.ar.base.config;

import com.ar.base.DTOs.ComprobanteConfirmadoEvent;
import com.ar.base.DTOs.FacturaProveedorEvent;
import com.ar.base.DTOs.ImporteAplicadoEvent;
import com.ar.base.DTOs.MinutaDePagoEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaConsumerConfig(ObjectMapper objectMapper, KafkaTemplate<String, Object> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    private Map<String, Object> baseConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "contactos-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        return props;
    }

    public <T> ConsumerFactory<String, T> consumerFactory(Class<T> targetType) {
        JsonDeserializer<T> jsonDeserializer = new JsonDeserializer<>(targetType, objectMapper, false);
        jsonDeserializer.addTrustedPackages("*");
        jsonDeserializer.setUseTypeHeaders(false);
        jsonDeserializer.ignoreTypeHeaders();

        return new DefaultKafkaConsumerFactory<>(baseConfigs(), new StringDeserializer(), jsonDeserializer);
    }

    public <T> ConcurrentKafkaListenerContainerFactory<String, T> kafkaListenerContainerFactory(Class<T> targetType) {
        ConcurrentKafkaListenerContainerFactory<String, T> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(targetType));
        factory.setCommonErrorHandler(errorHandler());
        return factory;
    }

    @Bean
    public CommonErrorHandler errorHandler() {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                (record, ex) -> new TopicPartition(record.topic() + ".DLT", record.partition()));

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 5));
        errorHandler.addNotRetryableExceptions(IllegalArgumentException.class);
        return errorHandler;
    }

    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer() {
        return new DeadLetterPublishingRecoverer(kafkaTemplate);
    }

    // Factories registradas por bean name
    @Bean(name = "facturaProveedorFactory")
    public ConcurrentKafkaListenerContainerFactory<String, FacturaProveedorEvent> facturaProveedorFactory() {
        return kafkaListenerContainerFactory(FacturaProveedorEvent.class);
    }

    @Bean(name = "reciboRecibidoFactory")
    public ConcurrentKafkaListenerContainerFactory<String, ComprobanteConfirmadoEvent> pagoRecibidoFactory() {
        return kafkaListenerContainerFactory(ComprobanteConfirmadoEvent.class);
    }

    @Bean(name = "comprobanteRecibidoFactory")
    public ConcurrentKafkaListenerContainerFactory<String, ComprobanteConfirmadoEvent> comprobanteFactory() {
        return kafkaListenerContainerFactory(ComprobanteConfirmadoEvent.class);
    }

    @Bean(name = "minutaDePagoFactory")
    public ConcurrentKafkaListenerContainerFactory<String, MinutaDePagoEvent> minutaFactory() {
        return kafkaListenerContainerFactory(MinutaDePagoEvent.class);
    }

//    @Bean(name = "comprobanteSaldadoFactory")
//    public ConcurrentKafkaListenerContainerFactory<String, ImporteAplicadoEvent> comprobanteSaldadoFactory() {
//        return kafkaListenerContainerFactory(ImporteAplicadoEvent.class);
//    }
}

package com.example.kafka_timestamp_processor;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaTimestampProcessor {
	
	 public static void main(String[] args) {
	        // Kafka Consumer properties
	        Properties consumerProps = new Properties();
	        consumerProps.put("bootstrap.servers", "localhost:9092");
	        consumerProps.put("key.deserializer", StringDeserializer.class.getName());
	        consumerProps.put("value.deserializer", StringDeserializer.class.getName());
	        consumerProps.put("group.id", "timestampProcessorGroup");

	        // Kafka Producer properties
	        Properties producerProps = new Properties();
	        producerProps.put("bootstrap.servers", "localhost:9092");
	        producerProps.put("key.serializer", StringSerializer.class.getName());
	        producerProps.put("value.serializer", StringSerializer.class.getName());

	        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
	        KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps);

	        String inputTopic = "input_topic";
	        String outputTopic = "output_topic";

	        consumer.subscribe(Collections.singletonList(inputTopic));

	        ObjectMapper objectMapper = new ObjectMapper();

	        try {
	            while (true) {
	                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
	                for (ConsumerRecord<String, String> record : records) {
	                    String message = record.value();

	                    try {
	                        // Parse the JSON message
	                        JsonNode jsonNode = objectMapper.readTree(message);
	                        long timestamp = jsonNode.get("timestamp").asLong();

	                        // Publish the timestamp to the output topic
	                        producer.send(new ProducerRecord<>(outputTopic, String.valueOf(timestamp)));

	                        System.out.println("Published timestamp: " + timestamp);
	                    } catch (Exception e) {
	                        System.err.println("Error processing message: " + e.getMessage());
	                    }
	                }
	            }
	        } finally {
	            consumer.close();
	            producer.close();
	        }
	    }
	
	

}

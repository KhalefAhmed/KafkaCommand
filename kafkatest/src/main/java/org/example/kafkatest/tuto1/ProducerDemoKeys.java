package org.example.kafkatest.tuto1;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class ProducerDemoKeys {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        Logger logger = LoggerFactory.getLogger(ProducerDemoKeys.class);

        String bootstrapServer = "127.0.0.1:9092";
        // create Producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServer);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());

        //create the producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);


        for (int i=0; i<10; i++) {
            //create a producer record

            String topic = "first_topic";
            String value = "hello world "+ Integer.toString(i);
            String key = "id_"+ Integer.toString(i);
            ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic,key, value);

            logger.info("Key: "+key); // log the key
            //id_0 is going to partition 1

            //send data - asynchronous
            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    //execute every time a record is successfully sent or an exception is thrown
                    if (e == null) {
                        //the record was successfully sent
                        logger.info("Received new metadata. \n" +
                                "Topic: " + recordMetadata.topic() + " \n" +
                                "Partition: " + recordMetadata.partition() + " \n" +
                                "Offset: " + recordMetadata.offset() + " \n" +
                                "Timestamp: " + recordMetadata.timestamp());
                    } else {
                        logger.error("error while producing ", e);
                    }
                }
            }).get(); // block the send. to make it synchronous - don't do that in production!
        }

        //flush data
        producer.flush();

        //flush and close producer
        producer.close();
    }
}

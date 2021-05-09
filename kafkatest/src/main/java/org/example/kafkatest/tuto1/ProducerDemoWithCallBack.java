package org.example.kafkatest.tuto1;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemoWithCallBack {

    public static void main(String[] args) {

        Logger logger = LoggerFactory.getLogger(ProducerDemoWithCallBack.class);

        String bootstrapServer = "127.0.0.1:9092";
        // create Producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServer);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());

        //create the producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        //create a producer record
        ProducerRecord<String,String> record = new ProducerRecord<String, String>("first_topic","hello world");

        //send data - asynchrone
        producer.send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                //execute every time a record is successfully sent or an exception is thrown
                if (e == null){
                    //the record was successfully sent
                    logger.info("Received new metadata. \n" +
                            "Topic: "+recordMetadata.topic()+" \n"+
                            "Partition: "+recordMetadata.partition()+" \n"+
                            "Offset: "+recordMetadata.offset()+" \n"+
                            "Timestamp: "+recordMetadata.timestamp());
                }
                else {
                    logger.error("error while producing " , e);
                }
            }
        });

        //flush data
        producer.flush();

        //flush and close producer
        producer.close();
    }
}

#!/usr/bin/env bash

######Windows

# Start the Zookeeper Service
zookeeper-server-start.bat config/zookeeper.properties

# Start the Kafka
kafka-server-start.bat config/server.properties

# Create input topic with two partitions
kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 2 --topic word-count-input

# Create output topic with two partitions
kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 2 --topic word-count-output

# launch a Kafka consumer
kafka-console-consumer.bat --bootstrap-server localhost:9092 ^
    --topic word-count-output ^
    --from-beginning ^
    --formatter kafka.tools.DefaultMessageFormatter ^
    --property print.key=true ^
    --property print.value=true ^
    --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer ^
    --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer

# produce data
kafka-console-producer.bat --broker-list localhost:9092 --topic word-count-input

# run the stream application
java -jar <application-name>.jar

######Mac/Linux

# Start the Zookeeper Service
zookeeper-server-start.sh config/zookeeper.properties

# Start the Kafka
kafka-server-start.sh config/server.properties

# create input topic with two partitions
kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 2 --topic word-count-input

# create output topic
kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 2 --topic word-count-output

# launch a Kafka consumer
kafka-console-consumer.sh --bootstrap-server localhost:9092 \
    --topic word-count-output \
    --from-beginning \
    --formatter kafka.tools.DefaultMessageFormatter \
    --property print.key=true \
    --property print.value=true \
    --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
    --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer

# produce data
kafka-console-producer.sh --broker-list localhost:9092 --topic word-count-input

# run the stream application
java -jar <application-name>.jar
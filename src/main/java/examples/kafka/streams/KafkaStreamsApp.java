package examples.kafka.streams;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Arrays;
import java.util.Properties;

public class KafkaStreamsApp {
    public static void main(String[] args) {

        Properties properties = new Properties();

        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "Stream-App");
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());


        StreamsBuilder streamsBuilder = new StreamsBuilder();

        //input Stream from Kafka
        KStream<String, String> wordCountStream = streamsBuilder.stream("word-count-input");

        // map values to lowercase
        KTable<String, Long> wordCounTable =
                wordCountStream.mapValues(values -> values.toLowerCase()).
                        // flatMapValues
                                flatMapValues(values -> Arrays.asList(values.split(" "))).
                        // select the keys
                                selectKey((ignoreKey, value) -> value).
                        //groupByKey
                                groupByKey().
                        //count the keys
                                count();

        // Write the results back the kafka topic
        wordCounTable.toStream().to("word-count-output", Produced.with(Serdes.String(), Serdes.Long()));

        KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(),properties);

        kafkaStreams.start();

        //Printing the topology to know about the sequence of streams and processors.
        System.out.println(kafkaStreams.toString());

        //shutdown hook to close the stream application.

        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));


    }
}

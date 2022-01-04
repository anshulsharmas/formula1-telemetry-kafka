/*
 * Copyright Paolo Patierno.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.ppatierno.formula1;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Aggregator;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Initializer;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindowedKStream;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.apache.kafka.streams.kstream.Windowed;

import java.time.Duration;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import org.jboss.logging.Logger;

@ApplicationScoped
public class F1StreamsTopology {

    private static final String DEFAULT_F1_STREAMS_INPUT_TOPIC = "f1-telemetry-drivers";
    private static final String DEFAULT_F1_STREAMS_OUTPUT_TOPIC = "f1-telemetry-drivers-avg-speed";
    
    private static Logger log = Logger.getLogger(F1StreamsTopology.class.getName());

    @Produces
    public Topology buildTopology() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        Serde<Driver> driverSerdes = Serdes.serdeFrom(new DriverSerializer(), new DriverDeserializer());
        Serde<SpeedCountAndSum> speedCountAndSumSerde = Serdes.serdeFrom(new SpeedCountAndSumSerializer(), new SpeedCountAndSumDeserializer());

        KStream<String, Integer> speedStream =
        streamsBuilder
                .stream(DEFAULT_F1_STREAMS_INPUT_TOPIC, Consumed.with(Serdes.String(), driverSerdes))
                .filter((driverid, driver) -> driver.hasValidTelemetry())
                .map((driverid, driver) -> new KeyValue<>(driver.getHashtag(), driver.getCarTelemetryData().getSpeed()));

        KGroupedStream<String, Integer> speedStreamGrouped =
                speedStream.groupByKey(Grouped.with(Serdes.String(), Serdes.Integer()));

        TimeWindowedKStream<String, Integer> speedStreamWindowed =
                speedStreamGrouped.windowedBy(TimeWindows.of(Duration.ofMillis(5000)));

        KTable<Windowed<String>, SpeedCountAndSum> speedCountAndSum =
                speedStreamWindowed.aggregate(new Initializer<SpeedCountAndSum>() {
                    @Override
                    public SpeedCountAndSum apply() {
                        return new SpeedCountAndSum(0, 0);
                    }
                }, new Aggregator<String, Integer, SpeedCountAndSum>() {
                    @Override
                    public SpeedCountAndSum apply(String key, Integer value, SpeedCountAndSum aggregate) {
                        aggregate.setCount(aggregate.getCount() + 1);
                        aggregate.setSum(aggregate.getSum() + value);
                        return aggregate;
                    }
                }, Materialized.with(Serdes.String(), speedCountAndSumSerde));

        KTable<Windowed<String>, Integer> speedAvarage =
                speedCountAndSum.mapValues(new ValueMapper<SpeedCountAndSum, Integer>() {
                    @Override
                    public Integer apply(SpeedCountAndSum speedCountAndSum) {
                        return speedCountAndSum.getSum() / speedCountAndSum.getCount();
                    }
                });

        speedAvarage
                .toStream()
                .map(new KeyValueMapper<Windowed<String>, Integer, KeyValue<? extends String, ? extends Integer>>() {
                    @Override
                    public KeyValue<? extends String, ? extends Integer> apply(Windowed<String> stringWindowed, Integer integer) {
                        return new KeyValue<>(stringWindowed.key(), integer);
                    }
                })
                .to(DEFAULT_F1_STREAMS_OUTPUT_TOPIC, Produced.with(Serdes.String(), Serdes.Integer()));
                //.print(Printed.toSysOut());
        

        Topology topology = streamsBuilder.build();
        log.infof("%s", topology.describe());
        return topology;
    }
}

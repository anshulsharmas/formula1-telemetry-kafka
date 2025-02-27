/*
 * Copyright Paolo Patierno.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.ppatierno.formula1;

import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;

import io.ppatierno.formula1.config.KafkaBaseConfig;

public class F1ConsumerAppConfig extends KafkaBaseConfig {

    private static final String F1_DRIVERS_GROUP_ID_ENV = "F1_DRIVERS_GROUP_ID";
    private static final String F1_EVENTS_GROUP_ID_ENV = "F1_EVENTS_GROUP_ID";
    private static final String F1_DRIVERS_AVG_SPEED_GROUP_ID_ENV = "F1_DRIVERS_AVG_SPEED_GROUP_ID";
    private static final String F1_BEST_OVERALL_SECTOR_GROUP_ID_ENV = "F1_BEST_OVERALL_SECTOR_GROUP_ID";
    private static final String F1_DRIVERS_TOPIC_ENV = "F1_DRIVERS_TOPIC";
    private static final String F1_EVENTS_TOPIC_ENV = "F1_EVENTS_TOPIC";
    private static final String F1_DRIVERS_AVG_SPEED_TOPIC_ENV = "F1_DRIVERS_AVG_SPEED_TOPIC";
    private static final String F1_BEST_OVERALL_SECTOR_TOPIC_ENV = "F1_BEST_OVERALL_SECTOR_TOPIC";

    private static final String DEFAULT_F1_DRIVERS_GROUP_ID = "f1-drivers-group";
    private static final String DEFAULT_F1_EVENTS_GROUP_ID = "f1-events-group";
    private static final String DEFAULT_F1_DRIVERS_AVG_SPEED_GROUP_ID = "f1-drivers-avg-speed-group";
    private static final String DEFAULT_F1_BEST_OVERALL_SECTOR_GROUP_ID = "f1-best-overall-sector-group";
    private static final String DEFAULT_F1_DRIVERS_TOPIC = "f1-telemetry-drivers";
    private static final String DEFAULT_F1_EVENTS_TOPIC = "f1-telemetry-events";
    private static final String DEFAULT_F1_DRIVERS_AVG_SPEED_TOPIC = "f1-telemetry-drivers-avg-speed";
    private static final String DEFAULT_F1_BEST_OVERALL_SECTOR_TOPIC = "f1-telemetry-best-overall-sector";

    private final String f1DriversGroupId;
    private final String f1EventsGroupId;
    private final String f1DriversAvgSpeedGroupId;
    private final String f1BestOverallSectorGroupId;
    private final String f1DriversTopic;
    private final String f1EventsTopic;
    private final String f1DriversAvgSpeedTopic;
    private final String f1BestOverallSectorTopic;

    private F1ConsumerAppConfig(String kafkaBootstrapServers, boolean kafkaTlsEnabled, String kafkaTruststoreLocation, String kafkaTruststorePassword,
                                String kafkaSaslMechanism, String kafkaSaslUsername, String kafkaSalsPassword,
                                String f1DriversGroupId, String f1EventsGroupId, String f1DriversAvgSpeedGroupId, String f1BestOverallSectorGroupId,
                                String f1DriversTopic, String f1EventsTopic, String f1DriversAvgSpeedTopic, String f1BestOverallSectorTopic) {
        super(kafkaBootstrapServers, kafkaTlsEnabled, kafkaTruststoreLocation, kafkaTruststorePassword, kafkaSaslMechanism, kafkaSaslUsername, kafkaSalsPassword);
        this.f1DriversGroupId = f1DriversGroupId;
        this.f1EventsGroupId = f1EventsGroupId;
        this.f1DriversAvgSpeedGroupId = f1DriversAvgSpeedGroupId;
        this.f1BestOverallSectorGroupId = f1BestOverallSectorGroupId;
        this.f1DriversTopic = f1DriversTopic;
        this.f1EventsTopic = f1EventsTopic;
        this.f1DriversAvgSpeedTopic = f1DriversAvgSpeedTopic;
        this.f1BestOverallSectorTopic = f1BestOverallSectorTopic;
    }

    public static F1ConsumerAppConfig fromEnv() {
        String kafkaBootstrapServers = System.getenv(KAFKA_BOOTSTRAP_SERVERS_ENV) == null ? DEFAULT_KAFKA_BOOTSTRAP_SERVERS : System.getenv(KAFKA_BOOTSTRAP_SERVERS_ENV);
        boolean kafkaTlsEnabled = System.getenv(KAFKA_TLS_ENABLED) == null ? DEFAULT_KAFKA_TLS_ENABLED : Boolean.parseBoolean(System.getenv(KAFKA_TLS_ENABLED));
        String kafkaTruststoreLocation = System.getenv(KAFKA_TRUSTSTORE_LOCATION_ENV);
        String kafkaTruststorePassword = System.getenv(KAFKA_TRUSTSTORE_PASSWORD_ENV);
        String kafkaSaslMechanism = System.getenv(KAFKA_SASL_MECHANISM);
        String kafkaSaslUsername = System.getenv(KAFKA_SASL_USERNAME);
        String kafkaSaslPassword = System.getenv(KAFKA_SASL_PASSWORD);
        String f1DriversGroupId = System.getenv(F1_DRIVERS_GROUP_ID_ENV) == null ? DEFAULT_F1_DRIVERS_GROUP_ID : System.getenv(F1_DRIVERS_GROUP_ID_ENV);
        String f1EventsGroupId = System.getenv(F1_EVENTS_GROUP_ID_ENV) == null ? DEFAULT_F1_EVENTS_GROUP_ID : System.getenv(F1_EVENTS_GROUP_ID_ENV);
        String f1DriversAvgSpeedGroupId = System.getenv(F1_DRIVERS_AVG_SPEED_GROUP_ID_ENV) == null ? DEFAULT_F1_DRIVERS_AVG_SPEED_GROUP_ID : System.getenv(F1_DRIVERS_AVG_SPEED_GROUP_ID_ENV);
        String f1BestOverallSectorGroupId = System.getenv(F1_BEST_OVERALL_SECTOR_GROUP_ID_ENV) == null ? DEFAULT_F1_BEST_OVERALL_SECTOR_GROUP_ID : System.getenv(F1_BEST_OVERALL_SECTOR_GROUP_ID_ENV);
        String f1DriversTopic = System.getenv(F1_DRIVERS_TOPIC_ENV) == null ? DEFAULT_F1_DRIVERS_TOPIC : System.getenv(F1_DRIVERS_TOPIC_ENV);
        String f1EventsTopic = System.getenv(F1_EVENTS_TOPIC_ENV) == null ? DEFAULT_F1_EVENTS_TOPIC : System.getenv(F1_EVENTS_TOPIC_ENV);
        String f1DriversAvgSpeedTopic = System.getenv(F1_DRIVERS_AVG_SPEED_TOPIC_ENV) == null ? DEFAULT_F1_DRIVERS_AVG_SPEED_TOPIC : System.getenv(F1_DRIVERS_AVG_SPEED_TOPIC_ENV);
        String f1BestOverallSectorTopic = System.getenv(F1_BEST_OVERALL_SECTOR_TOPIC_ENV) == null ? DEFAULT_F1_BEST_OVERALL_SECTOR_TOPIC : System.getenv(F1_BEST_OVERALL_SECTOR_TOPIC_ENV);
        return new F1ConsumerAppConfig(kafkaBootstrapServers, kafkaTlsEnabled, kafkaTruststoreLocation, kafkaTruststorePassword,
                                        kafkaSaslMechanism, kafkaSaslUsername, kafkaSaslPassword,
                                        f1DriversGroupId, f1EventsGroupId, f1DriversAvgSpeedGroupId, f1BestOverallSectorGroupId, f1DriversTopic, f1EventsTopic, f1DriversAvgSpeedTopic, f1BestOverallSectorTopic);
    }

    public static Properties getProperties(F1ConsumerAppConfig config) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getKafkaBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, config.getF1DriversGroupId());
        if (config.isKafkaTlsEnabled()) {
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
            if (config.getKafkaTruststoreLocation() != null && config.getKafkaTruststorePassword() != null) {
                props.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, "PKCS12");
                props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, config.getKafkaTruststoreLocation());
                props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, config.getKafkaTruststorePassword());
            }
        }

        if ("PLAIN".equals(config.getKafkaSaslMechanism()) && 
            config.getKafkaSaslUsername() != null && config.getKafkaSaslPassword() != null) {
                props.put(SaslConfigs.SASL_MECHANISM, config.getKafkaSaslMechanism());
                props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL".equals(props.getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG)) ? "SASL_SSL" : "SASL_PLAINTEXT");
                String saslJaasConfig = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + config.getKafkaSaslUsername() + "\" password=\"" + config.getKafkaSaslPassword() + "\";";
                props.put(SaslConfigs.SASL_JAAS_CONFIG, saslJaasConfig);
        }
        return props;
    }

    public String getF1DriversGroupId() {
        return f1DriversGroupId;
    }

    public String getF1EventsGroupId() {
        return f1EventsGroupId;
    }

    public String getF1DriversAvgSpeedGroupId() {
        return f1DriversAvgSpeedGroupId;
    }

    public String getF1BestOverallSectorGroupId() {
        return f1BestOverallSectorGroupId;
    }

    public String getF1DriversTopic() {
        return f1DriversTopic;
    }

    public String getF1EventsTopic() {
        return f1EventsTopic;
    }

    public String getF1DriversAvgSpeedTopic() {
        return f1DriversAvgSpeedTopic;
    }

    public String getF1BestOverallSectorTopic() {
        return f1BestOverallSectorTopic;
    }

    @Override
    public String toString() {
        return "F1ConsumerAppConfig[" +
                "kafkaBootstrapServers=" + this.kafkaBootstrapServers +
                ", kafkaTlsEnabled=" + this.kafkaTlsEnabled +
                ", kafkaTruststoreLocation=" +  this.kafkaTruststoreLocation +
                ", kafkaTruststorePassword=" +  this.kafkaTruststorePassword +
                ", kafkaSaslMechanism=" +  this.kafkaSaslMechanism +
                ", kafkaSaslUsername=" +  this.kafkaSaslUsername +
                ", kafkaSaslPassword=" +  this.kafkaSaslPassword +
                ", f1DriversGroupId=" + this.f1DriversGroupId +
                ", f1EventsGroupId=" + this.f1EventsGroupId +
                ", f1DriversAvgSpeedGroupId=" + this.f1DriversAvgSpeedGroupId +
                ", f1BestOverallSectorGroupId=" + this.f1BestOverallSectorGroupId +
                ", f1DriversTopic=" + this.f1DriversTopic +
                ", f1EventsTopic=" + this.f1EventsTopic +
                ", f1DriversAvgSpeedTopic=" + this.f1DriversAvgSpeedTopic +
                ", f1BestOverallSectorTopic=" + this.f1BestOverallSectorTopic +
                "]";
    }
}

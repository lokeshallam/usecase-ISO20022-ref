package com.demo.remittance;

import com.demo.remittance.msg.definitions.*;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.StringSerializer;

public class AvroProducer {

  public static void main(final String[] args) throws IOException {

    if (args.length != 1) {
      // Backwards compatibility, assume localhost
      System.out.println("Please provide the configuration file path as a command line argument");
      System.exit(1);
    }

    final Properties props = loadConfig(args[0]);
    final String topic = "payment-iso-remittance";

    props.put(ProducerConfig.RETRIES_CONFIG, 0);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
    props.put(AbstractKafkaSchemaSerDeConfig.NORMALIZE_SCHEMAS, true);

    try (KafkaProducer<String, RemittanceAdviceV05> producer = new KafkaProducer<>(props)) {
      final long numMessages = 10L;
      for (long i = 0; i < numMessages; i++) {
        UUID uuid = UUID.randomUUID();
        final String keyId = uuid.toString();

        final RemittanceAdviceV05 remittanceAdviceV05 = generateRandomRemittanceAdvice();

        final ProducerRecord<String, RemittanceAdviceV05> record =
            new ProducerRecord<>(topic, keyId, remittanceAdviceV05);
        producer.send(record);
        Thread.sleep(1000L);
      }

      producer.flush();
      System.out.printf(
          "Successfully produced %s messages to a topic called %s%n", numMessages, topic);

    } catch (final SerializationException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  // We'll reuse this function to load properties from the Consumer as well
  public static Properties loadConfig(final String configFile) throws IOException {
    if (!Files.exists(Paths.get(configFile))) {
      throw new IOException(configFile + " not found.");
    }
    final Properties cfg = new Properties();
    try (InputStream inputStream = new FileInputStream(configFile)) {
      cfg.load(inputStream);
    }
    return cfg;
  }

  static RemittanceAdviceV05 generateRandomRemittanceAdvice() {
    RemittanceAdviceV05 remittanceAdviceV05 = new RemittanceAdviceV05();

    GroupHeader79 groupHeader79 = new GroupHeader79();

    groupHeader79.setMsgId("MyRemittanceRecord" + UUID.randomUUID());
    groupHeader79.setCreDtTm(System.currentTimeMillis());
    PartyIdentification135 partyIdentification135 = new PartyIdentification135();

    groupHeader79.setInitgPty(partyIdentification135);

    remittanceAdviceV05.setGrpHdr(groupHeader79);

    RemittanceInformation20 remittanceInformation20 = new RemittanceInformation20();
    OriginalPaymentInformation9 originalPaymentInformation9 = new OriginalPaymentInformation9();
    TransactionReferences5 transactionReferences5 = new TransactionReferences5();
    transactionReferences5.setEndToEndId("endtoEndID");
    originalPaymentInformation9.setRefs(transactionReferences5);

    remittanceInformation20.setOrgnlPmtInf(originalPaymentInformation9);
    remittanceAdviceV05.setRmtInf(Collections.singletonList(remittanceInformation20));

    return remittanceAdviceV05;
  }
}

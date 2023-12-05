## Overview

The demo illustrates how ISO20022 complaint messages can be ingested into Kafka. 


## Requirements

- If you don't have a Confluent Cloud account, sign up for a free trial [here](https://www.confluent.io/confluent-cloud/tryfree).
- Please follow the instructions to install Terraform if it is not already installed on your system [here](https://developer.hashicorp.com/terraform/tutorials/aws-get-started/install-cli)


Step 1:

Deploy the Confluent Cloud environment using terraform 

```
cd terraform/DeployClusters
terraform plan

```
Review and apply the changes

```
terraform apply
```

Capture the API key and secret for Kafka cluster and Schema Registry cluster 

TODO  - Add commands to export the key and secret 
Also export kafka cluster id, environment and other details that can be reused in schema registration step.


Step 2:

Convert XSD schema to avro schema using any available options. In this example we used an open source version available.

Refer [here](./xml-avro/Readme.md) to generate the avro schema file from xsd file.

We used [remittance.xsd](https://www.iso20022.org/message/22296/download) in this example.




Step 3: Register schema Definition

We will now register the avro schema generated in above step in Confluent Schema Registry. 

Navigate to terraform folder 

```
cd terraform/RegisterSchemaDefinition

terraform plan
terraform apply

```

Schema is now registered in Confluent Schema Registry



Step 4:

In this step we will use Kafka client to produce and consume avro messages using the schema generated and registered in above steps


```
cd ProduceAndConsume
```
Copy the schema file generated to src/main/avro directory 

Run maven avro plugin to generate POJO files

```
mvn avro:schema
```



Prepare the required configuration file as below 

```
# Required connection configs for Kafka producer, consumer, and admin
bootstrap.servers=<<BOOTSTRAP-URL>>
security.protocol=SASL_SSL
sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required username='<<kakfa-api-key>>' password='<<Kafka-api-secret>>';
sasl.mechanism=PLAIN
# Required for correctness in Apache Kafka clients prior to 2.6
client.dns.lookup=use_all_dns_ips
?# Best practice for Kafka producer to prevent data loss
acks=all
?# Required connection configs for Confluent Cloud Schema Registry
schema.registry.url=<<SchemaRegistry-URL>>
basic.auth.credentials.source=USER_INFO
basic.auth.user.info=<<Schema-Registry-api-key>>:<<Schema-Registry-api-secret>>
```


Build the jar file using command

```
mvn clean compile package
```

Run the following command to execute the producer application, which will produce some random data events to the topic.
```
java -cp target/examples-1.0.jar com.demo.remittance.AvroProducer src/main/resources/cloud-config.properties
```



And consume events by running following command


```
java -cp target/examples-1.0.jar com.demo.remittance.AvroConsumer src/main/resources/cloud-config.properties
```


References:

Confluent Terraform Provider - https://registry.terraform.io/providers/confluentinc/confluent/latest/docs



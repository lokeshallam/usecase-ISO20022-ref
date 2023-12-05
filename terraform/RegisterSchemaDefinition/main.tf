terraform {
  required_providers {
    confluent = {
      source  = "confluentinc/confluent"
      version = "1.55.0"
    }
  }
}

provider "confluent" {
  cloud_api_key    = var.confluent_cloud_api_key
  cloud_api_secret = var.confluent_cloud_api_secret
}



data "confluent_schema_registry_cluster" "schema_registry" {
  id = "lsrc-1234"
  environment {
    id = "env-abc123"
  }

}

data "confluent_kafka_cluster" "basic" {
  id = "lkc-abc123"
  environment {
    id = "env-xyz456"
  }
}


resource "confluent_schema" "RemittanceAdviceV05" {
  schema_registry_cluster {
    id = data.confluent_schema_registry_cluster.schema_registry.id
  }
  rest_endpoint = data.confluent_schema_registry_cluster.schema_registry.rest_endpoint
  # https://developer.confluent.io/learn-kafka/schema-registry/schema-subjects/#topicnamestrategy
  subject_name = "${var.topic_name}-value"
  format = "AVRO"
  schema = file("../../xml-avro/example/RemittanceAdviceV05.avsc")
  credentials {
    key    = var.schema_registry_api_key
    secret = var.schema_registry_api_secret
  }
}
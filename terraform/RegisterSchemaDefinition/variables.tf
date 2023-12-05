variable "confluent_cloud_api_key" {
  description = "Confluent Cloud API Key"
  type        = string
  default = ""   #Add the Confluent Cloud API Key
}

variable "confluent_cloud_api_secret" {
  description = "Confluent Cloud API Secret"
  type        = string
  sensitive   = true
  default = ""    #Add the Confluent Cloud API secret
}


variable "topic_name" {
  description = "Confluent Cloud topic name"
  type        = string
  sensitive   = false
  default = ""    #Kafka Topic name
}

variable "schema_registry_api_key" {
  description = "Confluent Schema Registry API Key"
  type        = string
  default = ""   #Add the Confluent Cloud API Key
}

variable "schema_registry_api_secret" {
  description = "Confluent Schema Registry API Secret"
  type        = string
  sensitive   = true
  default = ""    #Add the Confluent Cloud API secret
}

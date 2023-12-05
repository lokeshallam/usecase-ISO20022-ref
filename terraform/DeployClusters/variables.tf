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

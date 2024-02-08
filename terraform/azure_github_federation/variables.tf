variable "github" {
  type = object({
    repository = string
  })
}

variable "subscription_id_dev" {
  type = string

  default = "1ab5e788-3b98-4c63-bd05-de0c7388c853"
}

variable "subscription_id_uat" {
  type = string

  default = "f47d50dc-b874-4e04-9d5c-c27f5053a651"
}

variable "subscription_id_prod" {
  type = string

  default = "813119d7-0943-46ed-8ebe-cebe24f9106c"
}

variable "tenant_id" {
  type = string

  default = "7788edaf-0346-4068-9d79-c868aed15b3d"
}

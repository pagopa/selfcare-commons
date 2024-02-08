terraform {
  required_version = ">=1.6.0"

  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = ">= 3.90.0"
    }

    github = {
      source  = "integrations/github"
      version = "5.45.0"
    }
  }
}

provider "azurerm" {
  alias = "dev"

  tenant_id       = local.tenant_id
  subscription_id = local.subscription_id_dev

  features {}
}

provider "azurerm" {
  alias = "uat"

  tenant_id       = local.tenant_id
  subscription_id = local.subscription_id_uat

  features {}
}

provider "azurerm" {
  alias = "prod"

  tenant_id       = local.tenant_id
  subscription_id = local.subscription_id_prod

  features {}
}

provider "github" {
  owner = "pagopa"
}

data "azurerm_client_config" "current" {}

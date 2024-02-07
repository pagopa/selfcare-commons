terraform {
  required_version = ">=1.6.0"

  backend "local" {
    
  }

  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "<= 3.90.0"
    }
    github = {
      source  = "integrations/github"
      version = "5.45.0"
    }
  }

  # backend "azurerm" {}
}

provider "azurerm" {
  features {}
}

provider "github" {
  owner = "pagopa"
}

data "azurerm_subscription" "current" {}

data "azurerm_client_config" "current" {}

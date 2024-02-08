locals {
  project = "selc-%s"

  key_vault_resource_group_name = "${local.project}-sec-rg"
  key_vault_name                = "${local.project}-kv"

  identity_ci_name             = "${local.project}-ms-github-ci-identity"
  identity_cd_name             = "${local.project}-ms-github-cd-identity"
  identity_resource_group_name = "${local.project}-identity-rg"

  repo_variables = {
    "AZURE_TENANT_ID" = data.azurerm_client_config.current.tenant_id,
  }

  repo_secrets = {
    "SONAR_TOKEN" = data.azurerm_key_vault_secret.key_vault_sonar.value,
  }

  dev = {
    ci = {
      protected_branches     = false
      custom_branch_policies = false
      variables = {
        "AZURE_SUBSCRIPTION_ID" = data.azurerm_subscriptions.subscription_dev.subscriptions[0].subscription_id
      }
      secrets = {
        "AZURE_CLIENT_ID" = data.azurerm_user_assigned_identity.identity_dev_ci.client_id
      }
    }
    cd = {
      protected_branches     = false
      custom_branch_policies = false
      variables = {
        "AZURE_SUBSCRIPTION_ID" = data.azurerm_subscriptions.subscription_dev.subscriptions[0].subscription_id
      }
      secrets = {
        "AZURE_CLIENT_ID" = data.azurerm_user_assigned_identity.identity_dev_cd.client_id
      }
    }
  }

  uat = {
    ci = {
      protected_branches     = false
      custom_branch_policies = true
      reviewers_teams        = ["selfcare-contributors"]
      branch_pattern         = "releases/*"
      variables = {
        "AZURE_SUBSCRIPTION_ID" = data.azurerm_subscriptions.subscription_uat.subscriptions[0].subscription_id
      }
      secrets = {
        "AZURE_CLIENT_ID" = data.azurerm_user_assigned_identity.identity_uat_ci.client_id
      }
    }
    cd = {
      protected_branches     = false
      custom_branch_policies = true
      reviewers_teams        = ["selfcare-contributors"]
      branch_pattern         = "releases/*"
      variables = {
        "AZURE_SUBSCRIPTION_ID" = data.azurerm_subscriptions.subscription_uat.subscriptions[0].subscription_id
      }
      secrets = {
        "AZURE_CLIENT_ID" = data.azurerm_user_assigned_identity.identity_uat_cd.client_id
      }
    }
  }

  prod = {
    ci = {
      protected_branches     = false
      custom_branch_policies = true
      reviewers_teams        = ["selfcare-contributors"]
      branch_pattern         = "releases/*"
      variables = {
        "AZURE_SUBSCRIPTION_ID" = data.azurerm_subscriptions.subscription_prod.subscriptions[0].subscription_id
      }
      secrets = {
        "AZURE_CLIENT_ID" = data.azurerm_user_assigned_identity.identity_prod_ci.client_id
      }
    }
    cd = {
      protected_branches     = false
      custom_branch_policies = true
      reviewers_teams        = ["selfcare-contributors"]
      branch_pattern         = "releases/*"
      variables = {
        "AZURE_SUBSCRIPTION_ID" = data.azurerm_subscriptions.subscription_prod.subscriptions[0].subscription_id
      }
      secrets = {
        "AZURE_CLIENT_ID" = data.azurerm_user_assigned_identity.identity_prod_cd.client_id
      }
    }
  }
}

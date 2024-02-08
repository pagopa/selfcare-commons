locals {
  project = "selc-%s"

  # cannot be moved below because of circular references
  dev_alias  = "d"
  uat_alias  = "u"
  prod_alias = "p"

  tenant_id            = "7788edaf-0346-4068-9d79-c868aed15b3d"
  subscription_id_dev  = "1ab5e788-3b98-4c63-bd05-de0c7388c853"
  subscription_id_uat  = "f47d50dc-b874-4e04-9d5c-c27f5053a651"
  subscription_id_prod = "813119d7-0943-46ed-8ebe-cebe24f9106c"

  key_vault_resource_group_name = "${local.project}-sec-rg"
  key_vault_name                = "${local.project}-kv"

  identity_resource_group_name = "${local.project}-identity-rg"
  identity_ci_name             = "${local.project}-ms-github-ci-identity"
  identity_cd_name             = "${local.project}-ms-github-cd-identity"

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
        "AZURE_SUBSCRIPTION_ID" = local.subscription_id_dev
      }
      secrets = {
        "AZURE_CLIENT_ID" = data.azurerm_user_assigned_identity.identity_dev_ci.client_id
      }
    }
    cd = {
      protected_branches     = false
      custom_branch_policies = false
      variables = {
        "AZURE_SUBSCRIPTION_ID" = local.subscription_id_dev
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
        "AZURE_SUBSCRIPTION_ID" = local.subscription_id_uat
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
        "AZURE_SUBSCRIPTION_ID" = local.subscription_id_uat
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
        "AZURE_SUBSCRIPTION_ID" = local.subscription_id_prod
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
        "AZURE_SUBSCRIPTION_ID" = local.subscription_id_prod
      }
      secrets = {
        "AZURE_CLIENT_ID" = data.azurerm_user_assigned_identity.identity_prod_cd.client_id
      }
    }
  }
}

data "github_organization_teams" "all" {
  root_teams_only = true
  summary_only    = true
}

data "github_team" "team_admins" {
  slug = "selfcare-admin"
}

data "azurerm_key_vault" "key_vault_dev" {
  provider = azurerm.dev
  name                = format(local.key_vault_name, "dev")
  resource_group_name = format(local.key_vault_resource_group_name, "dev")
}

data "azurerm_key_vault_secret" "key_vault_sonar" {
  name         = "sonar-token"
  key_vault_id = data.azurerm_key_vault.key_vault_dev.id
}

data "azurerm_user_assigned_identity" "identity_dev_ci" {
  provider = azurerm.dev

  name                = format(local.identity_ci_name, "dev")
  resource_group_name = format(local.identity_resource_group_name, "dev")
}

data "azurerm_user_assigned_identity" "identity_dev_cd" {
  provider = azurerm.dev

  name                = format(local.identity_ci_name, "dev")
  resource_group_name = format(local.identity_resource_group_name, "dev")
}

data "azurerm_user_assigned_identity" "identity_uat_ci" {
  provider = azurerm.uat

  name                = format(local.identity_ci_name, "uat")
  resource_group_name = format(local.identity_resource_group_name, "uat")
}

data "azurerm_user_assigned_identity" "identity_uat_cd" {
  provider = azurerm.uat
  name                = format(local.identity_ci_name, "uat")
  resource_group_name = format(local.identity_resource_group_name, "uat")
}

data "azurerm_user_assigned_identity" "identity_prod_ci" {
  provider = azurerm.prod

  name                = format(local.identity_ci_name, "prod")
  resource_group_name = format(local.identity_resource_group_name, "prod")
}

data "azurerm_user_assigned_identity" "identity_prod_cd" {
  provider = azurerm.prod

  name                = format(local.identity_ci_name, "prod")
  resource_group_name = format(local.identity_resource_group_name, "prod")
}

data "azurerm_subscriptions" "subscription_dev" {
  display_name_contains = "DEV-SelfCare"
}

data "azurerm_subscriptions" "subscription_uat" {
  display_name_contains = "UAT-SelfCare"
}

data "azurerm_subscriptions" "subscription_prod" {
  display_name_contains = "PROD-SelfCare"
}
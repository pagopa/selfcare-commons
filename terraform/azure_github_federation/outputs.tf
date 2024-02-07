output "tenant_id" {
  value = data.azurerm_client_config.current.tenant_id
}

output "subscription_id_dev" {
  value = data.azurerm_subscriptions.subscription_dev.subscriptions[0].subscription_id
}

output "subscription_id_uat" {
  value = data.azurerm_subscriptions.subscription_uat.subscriptions[0].subscription_id
}

output "subscription_id_prod" {
  value = data.azurerm_subscriptions.subscription_prod.subscriptions[0].subscription_id
}
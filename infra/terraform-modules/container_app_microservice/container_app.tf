resource "azapi_resource" "container_app" {
  type      = "Microsoft.App/containerApps@2023-05-01"
  name      = local.app_name
  location  = data.azurerm_resource_group.resource_group_app.location
  parent_id = data.azurerm_resource_group.resource_group_app.id

  tags = var.tags

  identity {
    type = "SystemAssigned"
  }

  body = jsonencode({
    properties = {
      configuration = {
        activeRevisionsMode = "Single"
        ingress = {
          allowInsecure = false
          external      = true
          traffic = [
            {
              latestRevision = true
              label          = "latest"
              weight         = 100
            }
          ]
          targetPort = 8080
        }
        secrets = local.secrets
      }
      environmentId = data.azurerm_container_app_environment.container_app_environment.id
      template = {
        containers = [
          {
            env   = concat(var.app_settings, local.secrets_env)
            image = "ghcr.io/pagopa/${var.image_name}:${var.image_tag}"
            name  = "${local.container_name}"
            resources = {
              cpu    = var.container_app.cpu
              memory = var.container_app.memory
            }
            probes = var.probes
          }
        ]
        scale = {
          maxReplicas = var.container_app.max_replicas
          minReplicas = var.container_app.min_replicas
          rules       = var.container_app.scale_rules
        }
      }
      workloadProfileName = var.workload_profile_name
    }
  })
}

resource "azurerm_key_vault_access_policy" "keyvault_containerapp_access_policy" {
  key_vault_id = data.azurerm_key_vault.key_vault.id
  tenant_id    = data.azurerm_client_config.current.tenant_id
  object_id    = azapi_resource.container_app.identity[0].principal_id

  secret_permissions = [
    "Get",
  ]
}

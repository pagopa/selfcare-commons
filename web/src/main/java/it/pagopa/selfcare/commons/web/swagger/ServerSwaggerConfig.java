package it.pagopa.selfcare.commons.web.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.servers.ServerVariable;
import io.swagger.v3.oas.models.servers.ServerVariables;
import springfox.documentation.oas.web.OpenApiTransformationContext;
import springfox.documentation.oas.web.WebMvcOpenApiTransformationFilter;
import springfox.documentation.spi.DocumentationType;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class ServerSwaggerConfig implements WebMvcOpenApiTransformationFilter {

    @Override
    public OpenAPI transform(OpenApiTransformationContext<HttpServletRequest> context) {
        OpenAPI openApi = context.getSpecification();
        final ServerVariable urlVariable = new ServerVariable();
        urlVariable.setDefault("http://localhost");
        final ServerVariable portVariable = new ServerVariable();
        portVariable.setDefault("8080");
        final ServerVariable basePathVariable = new ServerVariable();
        basePathVariable.setDefault("");
        final ServerVariables serverVariables = new ServerVariables();
        serverVariables.addServerVariable("url", urlVariable);
        serverVariables.addServerVariable("port", portVariable);
        serverVariables.addServerVariable("basePath", basePathVariable);
        Server server = new Server();
        server.setUrl("{url}:{port}{basePath}");
        server.setVariables(serverVariables);
        openApi.setServers(List.of(server));
        return openApi;
    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return documentationType.equals(DocumentationType.OAS_30);
    }
}

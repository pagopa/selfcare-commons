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
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerSwaggerConfig implements WebMvcOpenApiTransformationFilter {

    private static final Pattern PATTERN = Pattern.compile("^([^:]+:\\/\\/[^(?:\\:|\\/)]+)(?:(?:\\:(\\d+)\\/)|\\/).*$");


    @Override
    public OpenAPI transform(OpenApiTransformationContext<HttpServletRequest> context) {
        OpenAPI openApi = context.getSpecification();
        final ServerVariable urlVariable = new ServerVariable();
        final ServerVariable portVariable = new ServerVariable();
        final ServerVariable basePathVariable = new ServerVariable();
        final Matcher matcher = PATTERN.matcher(context.request()
                .map(HttpServletRequest::getRequestURL)
                .map(StringBuffer::toString)
                .orElse("http://localhost:8080"));
        if (matcher.matches()) {
            urlVariable.setDefault(matcher.group(1));
            portVariable.setDefault(Optional.ofNullable(matcher.group(2)).orElse("80"));
        }
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

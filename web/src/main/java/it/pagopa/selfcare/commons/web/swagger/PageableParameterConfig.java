package it.pagopa.selfcare.commons.web.swagger;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Pageable;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;

import java.util.ArrayList;
import java.util.List;

public class PageableParameterConfig implements OperationBuilderPlugin {

    private final SpringDataWebProperties springDataWebProperties;
    private final TypeResolver resolver;


    public PageableParameterConfig(SpringDataWebProperties springDataWebProperties,
                                   TypeResolver resolver) {
        this.springDataWebProperties = springDataWebProperties;
        this.resolver = resolver;
    }

    @Override
    public void apply(OperationContext context) {
        List<ResolvedMethodParameter> methodParameters = context.getParameters();
        ResolvedType pageableType = resolver.resolve(Pageable.class);
        List<RequestParameter> parameters = new ArrayList<>();
        for (ResolvedMethodParameter methodParameter : methodParameters) {
            ResolvedType resolvedType = methodParameter.getParameterType();
            if (pageableType.equals(resolvedType)) {
                parameters.add(new RequestParameterBuilder()
                        .in(ParameterType.QUERY)
                        .name(springDataWebProperties.getPageable().getPageParameter())
                        .query(q -> q.model(m -> m.scalarModel(ScalarType.INTEGER)))
                        .description(String.format("The page number to access (%s indexed, defaults to %s)",
                                springDataWebProperties.getPageable().isOneIndexedParameters() ? "1" : "0",
                                springDataWebProperties.getPageable().isOneIndexedParameters() ? "1" : "0"))
                        .build());
                parameters.add(new RequestParameterBuilder()
                        .in(ParameterType.QUERY)
                        .name(springDataWebProperties.getPageable().getSizeParameter())
                        .query(q -> q.model(m -> m.scalarModel(ScalarType.INTEGER)))
                        .description(String.format("Number of records per page (defaults to %d, max %d)",
                                springDataWebProperties.getPageable().getDefaultPageSize(),
                                springDataWebProperties.getPageable().getMaxPageSize()))
                        .build());
                parameters.add(new RequestParameterBuilder()
                        .in(ParameterType.QUERY)
                        .name(springDataWebProperties.getSort().getSortParameter())
                        .query(q -> q.model(m -> m.collectionModel(c -> c.model(cm -> cm.scalarModel(ScalarType.STRING)))))
                        .description("Sorting criteria in the format: property(,asc|desc). Default sort order is ascending. Multiple sort criteria are supported.")
                        .build());
                context.operationBuilder().requestParameters(parameters);
            }
        }
    }

    @Override
    public boolean supports(DocumentationType delimiter) {
        return true;
    }
}

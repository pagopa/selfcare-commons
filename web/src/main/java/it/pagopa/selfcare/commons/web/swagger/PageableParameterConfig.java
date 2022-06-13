package it.pagopa.selfcare.commons.web.swagger;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import org.springframework.core.env.Environment;
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

    private final Environment environment;
    private final TypeResolver resolver;


    public PageableParameterConfig(Environment environment,
                                   TypeResolver resolver) {
        this.environment = environment;
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
                        .name("page")
                        .query(q -> q.model(m -> m.scalarModel(ScalarType.INTEGER)))
                        .description("Results page you want to retrieve (0..N)").build());
                parameters.add(new RequestParameterBuilder()
                        .in(ParameterType.QUERY)
                        .name("size")
                        .query(q -> q.model(m -> m.scalarModel(ScalarType.INTEGER)))
                        .description("Number of records per page").build());
                parameters.add(new RequestParameterBuilder()
                        .in(ParameterType.QUERY)
                        .name("sort")
                        .query(q -> q.model(m -> m.collectionModel(c -> c.model(cm -> cm.scalarModel(ScalarType.STRING)))))
                        .description("Sorting criteria in the format: property(,asc|desc). "
                                + "Default sort order is ascending. "
                                + "Multiple sort criteria are supported.")
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

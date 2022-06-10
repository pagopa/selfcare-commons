package it.pagopa.selfcare.commons.web.swagger;

import org.springframework.core.annotation.Order;
import springfox.bean.validators.plugins.Validators;
import springfox.documentation.builders.ModelSpecificationBuilder;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import javax.validation.constraints.Email;
import java.util.Optional;

@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
public class EmailAnnotationSwaggerPluginConfig implements ModelPropertyBuilderPlugin {

    private static final ModelSpecification EMAIL_MODEL_SPEC = new ModelSpecificationBuilder()
            .scalarModel(ScalarType.EMAIL)
            .build();


    @Override
    public void apply(ModelPropertyContext context) {
        Optional<Email> emailAnnotation = this.extractEmail(context);
        emailAnnotation.ifPresent(email -> {
            context.getSpecificationBuilder().type(EMAIL_MODEL_SPEC);
            context.getSpecificationBuilder().example("email@example.com");
        });
    }


    @Override
    public boolean supports(DocumentationType documentationType) {
        return true;
    }


    private Optional<Email> extractEmail(ModelPropertyContext context) {
        return Validators.annotationFromBean(context, Email.class)
                .map(Optional::of)
                .orElse(Validators.annotationFromField(context, Email.class));
    }

}

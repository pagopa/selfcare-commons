package it.pagopa.selfcare.commons.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
public class Page<T> {

    @ApiModelProperty(value = "The page content", required = true)
    @JsonProperty(required = true)
    @NotNull
    @Valid
    private List<T> content;

    @ApiModelProperty(value = "The total amount of elements", required = true)
    @JsonProperty(required = true)
    private long totalElements;

    @ApiModelProperty(value = "The number of total pages", required = true)
    @JsonProperty(required = true)
    private int totalPages;

    @ApiModelProperty(value = "The number of the current page", required = true)
    @JsonProperty(required = true)
    private int number;

    @ApiModelProperty(value = "The size of the page", required = true)
    @JsonProperty(required = true)
    private int size;

}

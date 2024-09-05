package com.witboost.provisioning.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.witboost.provisioning.model.common.FailedOperation;
import com.witboost.provisioning.model.common.Problem;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@Valid
@ToString
public class ReverseProvisionOperationRequest<T> {

    @NotNull
    private String useCaseTemplateId;

    @NotNull
    private String environment;

    private @Valid T params;

    @NotNull
    private JsonNode catalogInfo;

    public Either<FailedOperation, String> getComponentKind() {
        return Option.ofOptional(Optional.ofNullable(
                        catalogInfo.path("spec").path("mesh").path("kind").textValue()))
                .toEither(() -> {
                    var catalogInfoString = Try.of(() -> new ObjectMapper().writeValueAsString(catalogInfo))
                            .toOption();
                    return new FailedOperation(
                            "Error while extracting the component kind from the Reverse Provision input catalog info",
                            catalogInfoString.toJavaOptional(),
                            Optional.of("spec.mesh.kind"),
                            Collections.singletonList(
                                    new Problem("Couldn't retrieve 'kind' field for the component catalog info")));
                });
    }
}

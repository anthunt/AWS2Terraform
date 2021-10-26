package com.anthunt.terraform.generator.aws.service.vpc.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.services.ec2.model.Route;

import java.text.MessageFormat;
import java.util.Optional;

@Data
@ToString
@Builder
public class AWSRoute implements TerraformSource {
    private static final String TERRAFORM_RESOURCE_NAME = "aws_route";
    private Route route;
    private String routeTableId;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return MessageFormat.format("{0}_{1}",
                routeTableId,
                Optional.ofNullable(route.destinationCidrBlock())
                        .orElse(Optional.ofNullable(route.destinationIpv6CidrBlock())
                                .orElse(route.destinationPrefixListId())));
    }

    @Override
    public String getResourceName() {
        return getResourceId().replaceAll("\\.", "-")
                .replaceAll("/", "_");

    }
}

package com.anthunt.terraform.generator.aws.service.vpc.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import software.amazon.awssdk.services.ec2.model.RouteTableAssociation;

import java.text.MessageFormat;
import java.util.Optional;

@Data
@ToString
@Builder
public class AWSRouteTableAssociation implements TerraformSource {
    private static final String TERRAFORM_RESOURCE_NAME = "aws_route_table_association";
    private RouteTableAssociation routeTableAssociation;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return MessageFormat.format("{0}/{1}",
                Optional.ofNullable(routeTableAssociation.subnetId())
                        .orElse(routeTableAssociation.gatewayId()),
                routeTableAssociation.routeTableId());
    }

    @Override
    public String getResourceName() {
        return getResourceId().replaceAll("/", "-");
    }
}

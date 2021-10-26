package com.anthunt.terraform.generator.aws.service.vpc.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.ToString;
import software.amazon.awssdk.services.ec2.model.PropagatingVgw;
import software.amazon.awssdk.services.ec2.model.Tag;

import java.util.List;

@Data
@ToString
@Builder
public class AWSRouteTable implements TerraformSource {
    private static final String TERRAFORM_RESOURCE_NAME = "aws_route_table";
    @Singular
    private final List<AWSRouteTableAssociation> awsRouteTableAssociations;
    @Singular
    private final List<PropagatingVgw> propagatingVgws;
    private final String routeTableId;
    @Singular
    private final List<AWSRoute> awsRoutes;
    private final List<Tag> tags;
    private final String vpcId;
    private final String ownerId;

    private String subnetId;
    private String gatewayId;
    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return routeTableId;
    }

    @Override
    public String getResourceName() {
        return routeTableId;
    }
}

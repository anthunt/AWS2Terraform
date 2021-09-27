package com.anthunt.terraform.generator.aws.service.rds.model;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.ToString;
import software.amazon.awssdk.services.rds.model.DBSubnetGroup;
import software.amazon.awssdk.services.rds.model.Tag;

import java.util.List;

@Data
@Builder
@ToString
public class AWSRdsSubnetGroup {
    private DBSubnetGroup dbSubnetGroup;
    @Singular
    private List<Tag> tags;
}

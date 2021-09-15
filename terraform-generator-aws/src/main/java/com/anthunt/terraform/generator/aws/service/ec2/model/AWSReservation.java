package com.anthunt.terraform.generator.aws.service.ec2.model;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@Builder
public class AWSReservation {

    @Singular
    private List<AWSInstance> instances;

}

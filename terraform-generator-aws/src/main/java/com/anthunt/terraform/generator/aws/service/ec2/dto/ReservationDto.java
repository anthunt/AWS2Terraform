package com.anthunt.terraform.generator.aws.service.ec2.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@Builder
public class ReservationDto {

    @Singular
    private List<InstanceDto> instances;

}

package com.anthunt.terraform.generator.aws.service.ec2.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class CustomDescribeInstancesResponse {

    private List<CustomReservation> reservations;

}

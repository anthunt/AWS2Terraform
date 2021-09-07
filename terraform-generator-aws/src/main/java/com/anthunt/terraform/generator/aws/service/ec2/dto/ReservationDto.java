package com.anthunt.terraform.generator.aws.service.ec2.dto;

import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
public class ReservationDto {

    private List<InstanceDto> instances;

    public ReservationDto() {
        instances = new ArrayList<InstanceDto>();
    }

    public List<InstanceDto> getInstances() {
        return instances;
    }

    public void add(InstanceDto instanceDto) {
        instances.add(instanceDto);
    }

}

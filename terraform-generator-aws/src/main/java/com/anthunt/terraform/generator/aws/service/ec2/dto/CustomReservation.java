package com.anthunt.terraform.generator.aws.service.ec2.dto;

import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
public class CustomReservation {


    private List<CustomInstance> instances;

    public CustomReservation() {
        instances = new ArrayList<CustomInstance>();
    }

    public List<CustomInstance> getInstances() {
        return instances;
    }

    public void add(CustomInstance customInstance) {
        instances.add(customInstance);
    }

}

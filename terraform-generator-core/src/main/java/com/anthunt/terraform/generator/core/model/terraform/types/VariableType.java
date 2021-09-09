package com.anthunt.terraform.generator.core.model.terraform.types;

public enum VariableType {

    String("string"),
    Bool("bool"),
    Number("number"),
    Object("object"),
    Map("map"),
    List("list");

    private final String type;
    private VariableType childType;

    VariableType(String type) {
        this.type = type;
    }

}

package com.anthunt.terraform.generator.aws.service.rds.model;

import com.anthunt.terraform.generator.core.model.terraform.TerraformSource;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.ToString;
import software.amazon.awssdk.services.rds.model.OptionGroup;
import software.amazon.awssdk.services.rds.model.Tag;

import java.util.List;

@Data
@Builder
@ToString
public class AWSRdsOptionGroup implements TerraformSource {
    private static final String TERRAFORM_RESOURCE_NAME = "aws_db_option_group";
    private OptionGroup optionGroup;
    @Singular
    private List<Tag> tags;

    @Override
    public String getTerraformResourceName() {
        return TERRAFORM_RESOURCE_NAME;
    }

    @Override
    public String getResourceId() {
        return optionGroup.optionGroupName();
    }

    @Override
    public String getResourceName() {
        return optionGroup.optionGroupName();
    }
}

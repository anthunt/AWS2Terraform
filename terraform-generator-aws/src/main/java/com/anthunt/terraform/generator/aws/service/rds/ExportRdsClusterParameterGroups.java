package com.anthunt.terraform.generator.aws.service.rds;

import com.anthunt.terraform.generator.aws.command.CommonArgs;
import com.anthunt.terraform.generator.aws.command.ExtraArgs;
import com.anthunt.terraform.generator.aws.service.AbstractExport;
import com.anthunt.terraform.generator.aws.service.rds.model.AWSDBClusterParameterGroup;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFBlock;
import com.anthunt.terraform.generator.core.model.terraform.elements.TFString;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.DBClusterParameterGroup;
import software.amazon.awssdk.services.rds.model.DescribeDbClusterParameterGroupsResponse;
import software.amazon.awssdk.services.rds.model.DescribeDbClusterParametersRequest;
import software.amazon.awssdk.services.rds.model.Parameter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportRdsClusterParameterGroups extends AbstractExport<RdsClient> {

    @Override
    protected Maps<Resource> export(RdsClient client, CommonArgs commonArgs, ExtraArgs extraArgs) {

        List<AWSDBClusterParameterGroup> awsdbClusterParameterGroups = getDBClusterParameterGroups(client);

        return getResourceMaps(awsdbClusterParameterGroups);

    }

    List<AWSDBClusterParameterGroup> getDBClusterParameterGroups(RdsClient client) {

        DescribeDbClusterParameterGroupsResponse describeDbClustersResponse = client.describeDBClusterParameterGroups();
        return describeDbClustersResponse.dbClusterParameterGroups().stream()
                .filter(parameterGroup ->
                        !parameterGroup.dbClusterParameterGroupName().startsWith("default."))
                .peek(parameterGroup -> log.debug("parameterGroup => {}", parameterGroup))
                .map(parameterGroup -> AWSDBClusterParameterGroup.builder()
                        .dbClusterParameterGroup(parameterGroup)
                        .parameters(client.describeDBClusterParameters(DescribeDbClusterParametersRequest.builder()
                                .dbClusterParameterGroupName(parameterGroup.dbClusterParameterGroupName())
                                .build()).parameters().stream()
                                .peek(parameter -> log.debug("parameter => {}", parameter))
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    Maps<Resource> getResourceMaps(List<AWSDBClusterParameterGroup> awsDbClusterParameterGroups) {
        Maps.MapsBuilder<Resource> resourceMapsBuilder = Maps.builder();
        awsDbClusterParameterGroups.forEach(awsDbClusterParameterGroup -> {
            DBClusterParameterGroup parameterGroup = awsDbClusterParameterGroup.getDbClusterParameterGroup();
            List<Parameter> parameters = awsDbClusterParameterGroup.getParameters();

            resourceMapsBuilder.map(
                            Resource.builder()
                                    .api("aws_rds_cluster_parameter_group")
                                    .name(parameterGroup.dbClusterParameterGroupName())
                                    .argument("name", TFString.build(parameterGroup.dbClusterParameterGroupName()))
                                    .argument("family", TFString.build(parameterGroup.dbParameterGroupFamily()))
                                    .argument("description", TFString.build(parameterGroup.description()))
                                    .argumentIf(Optional.ofNullable(parameters).isPresent(),
                                            "parameter",
                                            parameters.stream()
                                                    .filter(p -> p.source().equalsIgnoreCase("modified"))
                                                    .map(parameter -> TFBlock.builder()
                                                            .argument("name", TFString.build(parameter.parameterName()))
                                                            .argument("value", TFString.build(parameter.parameterValue()))
                                                            .build())
                                                    .collect(Collectors.toList()))
                                    .build()
                    )
                    .build();

        });

        return resourceMapsBuilder.build();
    }

}

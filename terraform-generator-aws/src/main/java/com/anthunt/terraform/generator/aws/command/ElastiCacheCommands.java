package com.anthunt.terraform.generator.aws.command;

import com.anthunt.terraform.generator.aws.service.elasticache.ExportElastiCacheClusters;
import com.anthunt.terraform.generator.aws.service.elasticache.ExportElastiCacheReplicationGroups;
import com.anthunt.terraform.generator.aws.service.elasticache.ExportElastiCacheSubnetGroups;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import software.amazon.awssdk.services.elasticache.ElastiCacheClient;

import javax.validation.Valid;

@Slf4j
@ShellComponent
public class ElastiCacheCommands extends AbstractCommands {

    private ExportElastiCacheClusters exportElastiCacheClusters;

    private ExportElastiCacheReplicationGroups exportElastiCacheReplicationGroups;

    private ExportElastiCacheSubnetGroups exportElastiCacheSubnetGroups;

    public ElastiCacheCommands(ExportElastiCacheClusters exportElastiCacheClusters, ExportElastiCacheReplicationGroups exportElastiCacheReplicationGroups, ExportElastiCacheSubnetGroups exportElastiCacheSubnetGroups) {
        this.exportElastiCacheClusters = exportElastiCacheClusters;
        this.exportElastiCacheReplicationGroups = exportElastiCacheReplicationGroups;
        this.exportElastiCacheSubnetGroups = exportElastiCacheSubnetGroups;
    }


    @ShellMethod("Export terraform resources of elastiCache clusters.")
    public void elastiCacheClusters(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        exportElastiCacheClusters.exportTerraform(ElastiCacheClient.class, commonArgs);
    }

    @ShellMethod("Export terraform resources of elastiCache replication groups.")
    public void elastiCacheReplicationGroups(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        exportElastiCacheReplicationGroups.exportTerraform(ElastiCacheClient.class, commonArgs);
    }

    @ShellMethod("Export terraform resources of elastiCache subnet groups.")
    public void elastiCacheSubnetGroups(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        exportElastiCacheSubnetGroups.exportTerraform(ElastiCacheClient.class, commonArgs);
    }

}

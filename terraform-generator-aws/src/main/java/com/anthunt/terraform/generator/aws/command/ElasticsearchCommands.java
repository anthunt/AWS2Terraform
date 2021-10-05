package com.anthunt.terraform.generator.aws.command;

import com.anthunt.terraform.generator.aws.service.elasticsearch.ExportElasticsearchDomains;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import software.amazon.awssdk.services.elasticsearch.ElasticsearchClient;

import javax.validation.Valid;

@Slf4j
@ShellComponent
public class ElasticsearchCommands extends AbstractCommands {

    private ExportElasticsearchDomains exportElasticsearchDomains;

    public ElasticsearchCommands(ExportElasticsearchDomains exportElasticsearchDomains) {
        this.exportElasticsearchDomains = exportElasticsearchDomains;
    }


    @ShellMethod("Export terraform resources of Elasticsearch Domains.")
    public void elasticsearchDomains(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {
        exportElasticsearchDomains.exportTerraform(ElasticsearchClient.class, commonArgs);
    }
}

package com.anthunt.terraform.generator.aws.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.shell.CommandRegistry;
import org.springframework.shell.standard.CommandValueProvider;

@Configuration
public class AppConfig {

    /**
     * Not need for command auto completion.
     * It's already defined. See below.
     * @see org.springframework.shell.standard.StandardAPIAutoConfiguration#commandValueProvider
     */
    CommandValueProvider commandValueProvider(CommandRegistry commandRegistry) {
        return new CommandValueProvider(commandRegistry);
    }
}

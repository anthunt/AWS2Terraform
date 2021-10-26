package com.anthunt.terraform.generator.aws.command;

import com.anthunt.terraform.generator.aws.config.ConfigRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.text.MessageFormat;
import java.util.Optional;

@Slf4j
@ShellComponent
public class ConfigCommands extends AbstractCommands {

    @ShellMethod("Configure AWS2Terraform profile and region")
    public void config(@ShellOption(optOut = true) ConfigArgs configArgs) {
        if (configArgs.isNoArgs()) {
            ConfigRegistry configRegistry = ConfigRegistry.getInstance();
            System.out.println(MessageFormat.format("Region={0}, Profile={1}", configRegistry.getRegion(), configRegistry.getProfile()));
            return;
        }

        ConfigRegistry configRegistry = ConfigRegistry.getInstance();
        if (Optional.ofNullable(configArgs.getRegion()).isPresent()) {
            configRegistry.setRegion(configArgs.getRegion());
            System.out.println(MessageFormat.format("Region is set to {0}", configRegistry.getRegion()));
        }
        if (Optional.ofNullable(configArgs.getProfile()).isPresent()) {
            configRegistry.setProfile(configArgs.getProfile());
            System.out.println(MessageFormat.format("Profile is set to {0}", configRegistry.getProfile()));
        }
    }
}

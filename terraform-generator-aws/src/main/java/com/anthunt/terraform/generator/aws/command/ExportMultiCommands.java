package com.anthunt.terraform.generator.aws.command;

import com.anthunt.terraform.generator.aws.command.args.CommonArgs;
import com.anthunt.terraform.generator.aws.config.ConfigRegistry;
import com.anthunt.terraform.generator.aws.utils.ThreadUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.shell.Utils;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.util.ReflectionUtils;

import javax.validation.Valid;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@ShellComponent
@AllArgsConstructor
public class ExportMultiCommands extends AbstractCommands {

    ApplicationContext applicationContext;

    @ShellMethod("Export terraform resources of ec2 instances.")
    public void allAvailableResources(@ShellOption(optOut = true) @Valid CommonArgs commonArgs) {

        ConfigRegistry configRegistry = ConfigRegistry.getInstance();
        if (Optional.ofNullable(commonArgs.getRegion()).isEmpty() &&
                Optional.ofNullable(configRegistry.getRegion()).isEmpty()) {
            System.out.println("Region is not set!");
            return;
        }

        if (Optional.ofNullable(commonArgs.getProfile()).isEmpty() &&
                Optional.ofNullable(configRegistry.getProfile()).isEmpty()) {
            System.out.println("Profile is not set!");
            return;
        }
//        Collection<Converter> converters = applicationContext.getBeansOfType(Converter.class).values();
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(ShellComponent.class);
        log.debug("ConfigCommands.class.getName()=>{}", ConfigCommands.class.getName());
        List<String> targetBeanNames = beansWithAnnotation.entrySet().stream()
                .peek(entry -> log.debug("entry.getKey()={}", entry.getKey()))
                .peek(entry -> log.debug("getSimpleName={}", entry.getValue().getClass().getSimpleName()))
//                .peek(entry -> log.debug("getPackageName={}", entry.getValue().getClass().getPackageName()))
//                .peek(entry -> log.debug("getName={}", entry.getValue().getClass().getName()))
//                .peek(entry -> log.debug("getCanonicalName={}", entry.getValue().getClass().getCanonicalName()))
//                .peek(entry -> log.debug("getTypeName={}", entry.getValue().getClass().getTypeName()))
                .filter(entry -> {
                    String beanClassSimpleName = entry.getValue().getClass().getSimpleName();
                    String beanClassPackageName = entry.getValue().getClass().getPackageName();
                    return !beanClassSimpleName.equals(ConfigCommands.class.getSimpleName())
                                    && !beanClassSimpleName.equals(this.getClass().getSimpleName())
                                    && beanClassPackageName.equals(this.getClass().getPackageName());
                        }
                )
                .map(entry -> entry.getKey())
                .collect(Collectors.toList());
        log.debug("targetBeanNames=>{}", targetBeanNames);
        targetBeanNames.stream().forEach(targetBeanName -> {
            Object beanObject = beansWithAnnotation.get(targetBeanName);
            Method[] methods = beanObject.getClass().getDeclaredMethods();
            Arrays.stream(methods)
                    .peek(method -> log.debug("method=>{}, {}", targetBeanName, method.getName()))
                    .filter(method -> method.getAnnotation(ShellMethod.class) != null)
                    .forEach(method -> {
                                log.debug("invoke method=>{}, {}", targetBeanName, method.getName());
                                System.out.println(MessageFormat.format("{0} : {1}",
                                        beanObject.getClass().getSimpleName(),
                                        Utils.unCamelify(method.getName())));
                                ThreadUtils.sleep(200);
                                try {
                                    ReflectionUtils.invokeMethod(method, beanObject, commonArgs);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                    );
        });
    }
}

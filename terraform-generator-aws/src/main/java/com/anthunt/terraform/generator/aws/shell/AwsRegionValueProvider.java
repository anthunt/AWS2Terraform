package com.anthunt.terraform.generator.aws.shell;

import org.springframework.core.MethodParameter;
import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.shell.standard.ValueProviderSupport;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.Region;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AwsRegionValueProvider extends ValueProviderSupport {

    @Override
    public List<CompletionProposal> complete(MethodParameter methodParameter, CompletionContext completionContext, String[] hints) {

//        if (methodParameter.getParameterName().equals("region")) {
        String userInput = completionContext.currentWordUpToCursor();
        return Region.regions().stream()
                .filter(region -> region.id().startsWith(userInput))
                .map(region -> new CompletionProposal(region.id()))
                .collect(Collectors.toList());
//        } else {
//            return null;
//        }
    }
}

package com.anthunt.terraform.generator.aws.shell;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.shell.standard.ValueProviderSupport;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.profiles.ProfileFile;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AwsProfileValueProvider extends ValueProviderSupport {

    @Override
    public List<CompletionProposal> complete(MethodParameter methodParameter, CompletionContext completionContext, String[] hints) {
        log.debug("complete({},{},{})", methodParameter, completionContext, hints);
        String userInput = completionContext.currentWordUpToCursor();
        return ProfileFile.defaultProfileFile().profiles().keySet().stream()
                .filter(profile -> profile.startsWith(userInput))
                .map(profile -> new CompletionProposal(profile))
                .collect(Collectors.toList());
    }
}

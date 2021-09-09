package com.anthunt.terraform.generator.aws.support;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import software.amazon.awssdk.profiles.ProfileFile;

@Slf4j
public class DisabledOnNoAwsCredentialsCondition implements ExecutionCondition {

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        ProfileFile profileFile = ProfileFile.defaultProfileFile();
//        log.debug("profileFile => {}", profileFile);
        if (profileFile.profiles().size() > 0) {
            return ConditionEvaluationResult.enabled("Test enabled");
        } else {
            return ConditionEvaluationResult.disabled("Test disabled.");
        }
    }
}

package com.anthunt.terraform.generator.config;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

@Component
public class ExportPromptProvider implements PromptProvider {

    @Override
    public AttributedString getPrompt() {
        return new AttributedString("export:>",
                    AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN));
    }

}
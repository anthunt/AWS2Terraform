package com.anthunt.terraform.generator.aws.support;

import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;

public class TestDataFileUtils {

    public static String asString(Resource resource) {
        try {
            return FileCopyUtils.copyToString(
                    new InputStreamReader(
                            resource.getInputStream()
                    )
            ).replaceAll("\r\n", "\n");
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}

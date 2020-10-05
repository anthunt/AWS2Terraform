package com.anthunt.terraform.generator.aws.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class IOUtils {

    public static void writeFile(String dirPath, String fileName, String contents, boolean isSilence) {

        try {
            File dir = new File(dirPath);

            if (dir.mkdirs() && !isSilence) {
                log.info("make directory {}", dir.getAbsolutePath());
            }

            String filePath = dir.getAbsolutePath() + File.separator + fileName;
            Files.write(Paths.get(filePath), contents.getBytes());
            if(!isSilence) {
                log.info("terraform file created at {}", filePath);
            }

        } catch (IOException e) {
            log.error("write file failed.", e);
        }

    }

}

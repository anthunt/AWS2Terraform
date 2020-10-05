package com.anthunt.terraform.generator.aws.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

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

    public static void emptyDir(String outputDirPath) {
        try {
            Files.walk(Path.of(new File(outputDirPath).toURI()))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            log.error("cannot delete directory - {} [{}]", outputDirPath, e.getMessage());
        }
    }
}

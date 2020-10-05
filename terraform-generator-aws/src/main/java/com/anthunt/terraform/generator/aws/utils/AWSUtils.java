package com.anthunt.terraform.generator.aws.utils;

import software.amazon.awssdk.services.ec2.model.Tag;

import java.util.List;

public class AWSUtils {

    public static String getTag(List<Tag> tags, String keyName) {
        return tags.stream().filter(tag->keyName.equals(tag.key())).findFirst().get().value();
    }

}

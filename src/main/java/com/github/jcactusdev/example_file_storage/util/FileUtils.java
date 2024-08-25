package com.github.jcactusdev.example_file_storage.util;

import org.springframework.util.StringUtils;

import java.util.concurrent.ThreadLocalRandom;

public class FileUtils extends org.apache.tomcat.util.http.fileupload.FileUtils {

    public static String generateUniqueFileName(String fileName) {
        fileName = StringUtils.cleanPath(fileName);
        int index = fileName.lastIndexOf('.');
        String name = fileName.substring(0, index);
        String type = fileName.substring(index + 1);
        Long currentRandomValue = ThreadLocalRandom.current().nextLong();
        int resultLenght = name.length()
                + type.length()
                + currentRandomValue.toString().length();
        return String.format("%s%d.%s", (resultLenght < 255 ? name : name.substring(0, 255)), currentRandomValue, type);
    }

}
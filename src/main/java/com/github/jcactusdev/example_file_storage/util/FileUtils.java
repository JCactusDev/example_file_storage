package com.github.jcactusdev.example_file_storage.util;

import org.springframework.util.StringUtils;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class FileUtils extends org.apache.tomcat.util.http.fileupload.FileUtils {

    public static String generateUniqueFileName(String fileName) {
        int index = fileName.lastIndexOf('.');
        String name = fileName.substring(0, index);
        String format = fileName.substring(index + 1);
        Long currentRandomValue = ThreadLocalRandom.current().nextLong();

        int resultLenght = name.length()
                + format.length()
                + currentRandomValue.toString().length();

        return String.format("%s%d.%s", (resultLenght < 255 ? name : name.substring(0, 255)), currentRandomValue, format);
    }

    public static String generateRandomString(int length) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        return new Random().ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

}
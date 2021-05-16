package ai.learngram.video.utils;

import java.util.UUID;

public class TokenGenerator {

    public static String generate() {
        return UUID.randomUUID().toString();
    }
}

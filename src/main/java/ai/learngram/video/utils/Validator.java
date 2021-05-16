package ai.learngram.video.utils;

import java.util.regex.Pattern;

public class Validator {

    public static boolean validateEmail(String email) {
        Pattern pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        return pattern
                .matcher(email)
                .find();
    }

    public static boolean validatePassword(String password) {
        return password.length() < 16 && password.length() >= 3;
    }
}

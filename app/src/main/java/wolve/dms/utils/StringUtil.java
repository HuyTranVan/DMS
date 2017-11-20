package wolve.dms.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Created by macos on 11/6/17.
 */

public class StringUtil {
    public static String unAccent(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replaceAll("Đ", "D").replace("đ", "");
    }
}

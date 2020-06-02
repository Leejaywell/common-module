package pers.lee.common.rpc.server;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author YangYang
 * @version 0.1, 2008-3-18 16:17:45
 */
public class PatternUtils {
	
    public static String removeWhite(String string) {
        return replace(string, "\\s+", "");
    }

    public static String replace(String string, String patternString, String replaceString) {
        return replace(string, patternString, replaceString, null);
    }

    public static String replace(String string, String patternString, String replaceString, List<String> replacedList) {       
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(string);
        int index = 0;
        StringBuffer stringBuffer = new StringBuffer();
        while(index < string.length() && matcher.find(index)) {
            stringBuffer.append(string.substring(index, matcher.start()));
            stringBuffer.append(replaceString);
            if (replacedList != null) {
                replacedList.add(matcher.group());
            }
            index = matcher.end();
        }
        stringBuffer.append(string.substring(index));

        return stringBuffer.toString();
    }
    
}

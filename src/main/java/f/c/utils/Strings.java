package f.c.utils;

public class Strings {

    /**
     * 判断是否为null或者是空白字符
     * 
     * @param cs
     * @return
     */
    public static boolean isBlank(CharSequence cs) {
        if (null == cs)
            return true;
        int length = cs.length();
        for (int i = 0; i < length; i++) {
            if (!(Character.isWhitespace(cs.charAt(i))))
                return false;
        }
        return true;
    }

    /**
     * 将字符串首字母小写
     * 
     * @param s
     * @return
     */
    public static String lowerFirst(CharSequence s) {
        if (null == s)
            return null;
        int len = s.length();
        if (len == 0)
            return "";
        char c = s.charAt(0);
        if (Character.isLowerCase(c))
            return s.toString();
        return new StringBuilder(len).append(Character.toLowerCase(c)).append(s.subSequence(1, len)).toString();
    }

    /**
     * 将首字母大写
     * 
     * @param s
     * @return
     */
    public static String upperFirst(CharSequence s) {
        if (null == s)
            return null;
        int len = s.length();
        if (len == 0)
            return "";
        char c = s.charAt(0);
        if (Character.isUpperCase(c))
            return s.toString();
        return new StringBuilder(len).append(Character.toUpperCase(c)).append(s.subSequence(1, len)).toString();
    }
}

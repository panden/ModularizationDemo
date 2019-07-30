package com.lib.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串正则比对
 */
public final class MatchUtils {

    /**
     * 匹配蓝牙mac地址
     */
    public static boolean matchBleMac(String mac) {
        if (TextUtils.isEmpty(mac)) return false;
        String regex = "^([A-Za-z0-9]{2}:){5}[A-Za-z0-9]{2}$";
        return mac.matches(regex);
    }

    /**
     * 验证数字
     */
    public static boolean matchNumber(String str) {
        if (TextUtils.isEmpty(str)) return false;
        String numRegex = "^\\d+$";
        return str.matches(numRegex);
    }

    /**
     * 匹配账号密码格式(中英文8-16位)
     */
    public static boolean matchesUserPwd(String pwd) {
        if (TextUtils.isEmpty(pwd)) return false;
        String userPwdRegex = "[a-z0-9A-Z]{8,16}";
        return pwd.matches(userPwdRegex);
    }

    /**
     * 验证手机号格式
     */
    public static boolean matchMobileNum(String mobiles) {
        //"[1]"代表第1位为数字1，"[345678]"代表第二位可以为3、4、5、6、7、8、9中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        //不推荐使用很严格的验证，后续不知道电信部是否会开放其它号段的手机号。而代码需要不断的更新现有的匹配规则，模糊一点反而更方便维护
        String telRegex = "^[1][3456789]\\d{9}$";
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }

    /**
     * 验证18位身份证号
     */
    public static boolean matchIDCard(String idCard) {
        if (TextUtils.isEmpty(idCard)) return false;
        String idCardRegex = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9Xx])$";
        return match(idCardRegex, idCard);
    }

    /**
     * 判断邮编
     */
    public static boolean matchZipNO(String zipString) {
        if (TextUtils.isEmpty(zipString)) return false;
        String str = "^[1-9][0-9]{5}$";
        return Pattern.compile(str).matcher(zipString).matches();
    }

    /**
     * 判断邮箱是否合法
     */
    public static boolean matchEmail(String email) {
        if (TextUtils.isEmpty(email)) return false;
        //Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
        Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//复杂匹配
        Matcher m = p.matcher(email);
        return m.matches();
    }


    /**
     * 验证IP地址
     *
     * @param str :待验证的字符串
     * @return 如果是符合格式的字符串, 返回 <b>true </b>,否则为 <b>false </b>
     */
    public static boolean matchIP(String str) {
        String num = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";
        String regex = "^" + num + "\\." + num + "\\." + num + "\\." + num + "$";
        return match(regex, str);
    }


    /**
     * 验证非零的正整数
     */
    public static boolean matchNoZeroPositiveInteger(String str) {
        String regex = "^\\+?[1-9][0-9]*$";
        return match(regex, str);
    }

    /**
     * 验证大写字母
     */
    public static boolean matchUpChar(String str) {
        String regex = "^[A-Z]+$";
        return match(regex, str);
    }

    /**
     * 验证小写字母
     */
    public static boolean matchLowChar(String str) {
        String regex = "^[a-z]+$";
        return match(regex, str);
    }

    /**
     * 验证输入英文字符
     */
    public static boolean matchLetter(String str) {
        String regex = "^[A-Za-z]+$";
        return match(regex, str);
    }

    /**
     * 验证输入汉字
     */
    public static boolean matchChinese(String str) {
        String regex = "^[\u4e00-\u9fa5],{0,}$";
        return match(regex, str);
    }

    /**
     * @param regex 正则表达式字符串
     * @param str   要匹配的字符串
     * @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
     */
    private static boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
}

package com.le.matrix.redis.common;


import java.util.Random;

public class GenerateUtil {
    static char[] c = new char[]{'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P', 'Q','R','S','T','U','V','W','X', 'Y','Z','1','2','3','4','5','6','7','8','9','0'};

    /**
     * 随机生成十位字符串
     * @return
     */
    public static String generatePassword(){
         Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i= 0;i< 10;i++){
            sb.append(c[random.nextInt(c.length)]);
        }
        return sb.toString();
    }
}

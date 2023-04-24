package com.jandjdevlps.yedas.lib;

public class StringLib {
    /**
     * 문자열의 Null이나 문자 길이가 0을 판별해주는 함수
     * @param strValue 확인할 대상 문자열
     * @return 참 : 비어있거나 Null, 거짓 : 데이터 있음
     */
    public static boolean isNullorEmpty(String strValue)
    {
        return strValue == null || strValue.length() == 0;
    }
}

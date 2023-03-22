package com.ttk.openapi.sdk;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * @author lvaolin
 * @create 2019/2/1 10:17 AM
 */
@DisplayName("SDK字符串工具类测试")
public class TtkStringUtilTest {

    @DisplayName("字符串空判断")
    @Test
    void isNullOrEmpty(){
        Assertions.assertTrue(TtkStringUtil.isNullOrEmpty(null));
        Assertions.assertTrue(TtkStringUtil.isNullOrEmpty(""));
        Assertions.assertFalse(TtkStringUtil.isNullOrEmpty("a"));
        Assertions.assertFalse(TtkStringUtil.isNullOrEmpty(" "));
    }
}

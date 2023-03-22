package com.ttk.openapi.sdk;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * @author lvaolin
 * @create 2019/2/1 10:15 AM
 */
@DisplayName("加解密工具")
public class TtkOpenApiDesUtilTest {

    @DisplayName("加密与解密")
    @ParameterizedTest
    @ValueSource(strings = {"1111","~!~@#$%^&","PO:--=  ","中国","🇨🇳","😆","O(∩_∩)O哈哈~"})
    void cryption(String pwd){
        try {
            String before = pwd;
            String after = TtkOpenApiDesUtil.encryption(before);
            String before_ = TtkOpenApiDesUtil.decryption(after);
            System.out.println(before);
            System.out.println(after);
            System.out.println(before_);
            Assertions.assertEquals(before,before_);
        } catch (Exception e) {
            //有异常，单元测试失败
            Assertions.assertTrue(false);
        }

    }

}

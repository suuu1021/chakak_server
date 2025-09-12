package com.green.chakak.chakak._global.utils;

import com.green.chakak.chakak._global.errors.exception.Exception500;

import java.security.MessageDigest;
import java.util.Base64;

/**
 * 1.해시(hash) 사용(복호화 불가, 가장 일반적)
 * DB에 평문이 아닌 해시 문자열(예: BCrypt, SHA256)을저장
 * 비밀번호는 항상 이렇게 저장하는게 표준입니다.
 * 예시 (SHA-256, JDK 내장)
 */
public class HashUtil {

    public static String sha256(String input){

        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash =digest.digest(input.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e){
            throw new RuntimeException("SHA-256 해싱 오류", e);
        }
    }
}

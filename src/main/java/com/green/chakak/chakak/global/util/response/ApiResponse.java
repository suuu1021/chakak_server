package com.green.chakak.chakak.global.util.response;

public class ApiResponse<T> {

    private Integer status; // HTTP 상태 코드
    private String msg;     // 응답 메세지
    private T body;         // 실제 응답 데이터 (제네릭 사용)

    /**
     * 성공 응답 생성자
     */
    public ApiResponse(T body) {
        this.status = 200;
        this.msg = "성공";
        this.body = body;
    }

    /**
     * 실패 응답 생성자
     */
    public ApiResponse(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
        this.body = null;
    }

}
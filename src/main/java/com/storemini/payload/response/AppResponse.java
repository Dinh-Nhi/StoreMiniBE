package com.storemini.payload.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AppResponse {
    private int code;
    private String message;
    private Object data;

    public static AppResponse success(Object data) {
        return success("Thành công", data, 2000);
    }

    public static AppResponse success(String message, Object data) {
        return success(message, data, 2000);
    }

    public static AppResponse success(String message, Object data, int status) {
        return AppResponse.builder()
                .code(status)
                .message(message)
                .data(data)
                .build();
    }

    public static AppResponse error(String message) {
        return AppResponse.builder()
                .code(4000)
                .message(message)
                .build();
    }

    public static AppResponse error(String message, int code) {
        return AppResponse.builder()
                .code(code)
                .message(message)
                .build();
    }
    public AppResponse(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }


}

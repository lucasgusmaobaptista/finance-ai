package me.lucasgusmao.financeai.exceptions.response;

import java.util.List;

public record ResponseError(String message, List<FieldError> fieldErrors) {
    public static ResponseError defaultResponse(String message) {
        return new ResponseError(message, List.of());
    }

}

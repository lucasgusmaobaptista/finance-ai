package me.lucasgusmao.financeai.exceptions.handler;

import me.lucasgusmao.financeai.exceptions.custom.AlreadyExistsException;
import me.lucasgusmao.financeai.exceptions.custom.InvalidOperationException;
import me.lucasgusmao.financeai.exceptions.response.ResponseError;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class GlobalExceptionHandler {

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseError handleAlreadyExists(AlreadyExistsException e) {
        return ResponseError.defaultResponse(e.getMessage());
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ResponseError handleInvalidOperation(InvalidOperationException e) {
        return ResponseError.defaultResponse(e.getMessage());
    }
}

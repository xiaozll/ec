package com.eryansky.configure.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                         HttpHeaders headers, HttpStatus status, WebRequest request) {
        String method = ex.getMethod();
        String[] supportedMethods = ex.getSupportedMethods();
        String body = "不支持的请求类型：" + method + "，支持的请求类型：" + ((ServletWebRequest) request).getRequest().getRequestURI() + " - "+ Arrays.toString(supportedMethods);
        logger.error(body);
        Map<String, Object> map = new HashMap<>();
        map.put("body", body);
        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

}

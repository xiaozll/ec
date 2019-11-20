package com.eryansky.modules.sys.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Spring Boot 自定义错误页面
 */
@Controller
public class AppErrorController implements ErrorController {

    @Autowired
    private ErrorAttributes errorAttributes;

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, WebRequest webRequest, Model uiModel) {
//        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Map<String, Object> errorData = errorAttributes.getErrorAttributes(webRequest, true);
        Integer status = (Integer) errorData.get("status");        //请求路径
        uiModel.addAttribute("errorData", errorData);
        if (status != null) {
            int statusCode = status;
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "commons/404.html";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "commons/500.html";
            }
        }
        return "commons/error.html";
    }
}
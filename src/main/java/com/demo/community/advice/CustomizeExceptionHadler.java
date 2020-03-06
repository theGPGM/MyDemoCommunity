package com.demo.community.advice;

import com.alibaba.fastjson.JSON;
import com.demo.community.dto.ResultDTO;
import com.demo.community.exception.CustomizeErrorCode;
import com.demo.community.exception.CustomizeException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice
public class CustomizeExceptionHadler {

    @ExceptionHandler(Exception.class)

    ModelAndView handle(HttpServletRequest request,
                  HttpServletResponse response,
                  Throwable ex,
                  Model model) {
        String contentType = request.getContentType();
        if("application/json".equals(contentType)){
            ResultDTO resultDTO;
            if (ex instanceof CustomizeException){
                resultDTO = ResultDTO.errorOf((CustomizeException) ex);
            }else
                resultDTO = ResultDTO.errorOf(CustomizeErrorCode.SYS_ERROR);
            try{
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(resultDTO));
                writer.close();
            } catch (IOException e) {
            }

            return null;
        }
        else{
            if (ex instanceof CustomizeException){
                model.addAttribute("message", ex.getMessage());
            }else
                model.addAttribute("message",CustomizeErrorCode.SYS_ERROR.getMessage());
            return new ModelAndView("error");
        }

    }

}

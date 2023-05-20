package com.schoolmarket.market_server.handler;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.schoolmarket.market_server.untils.annotations.ExceptionCatch;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.ServletException;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.schoolmarket.market_server.untils.Re;

import java.io.IOException;
import java.net.SocketTimeoutException;

@Configuration
@ControllerAdvice
@ResponseBody
public class GlobalException extends RuntimeException {

    @ExceptionCatch
    @ExceptionHandler(SignatureVerificationException.class)
    public Object StException(SignatureVerificationException signatureVerificationException){
        return Re.SetStatus("证书签名异常");
    }


    @ExceptionCatch
    @ExceptionHandler(TokenExpiredException.class)
    public Object StException(TokenExpiredException tokenExpiredException){

        return Re.SetStatus("token过期");
    }

    @ExceptionCatch
    @ExceptionHandler(AlgorithmMismatchException.class)
    public Object StException(AlgorithmMismatchException AlgorithmMismatchException){;
        return Re.SetStatus("加密算法不匹配");
    }

    @ExceptionCatch
    @ExceptionHandler(SocketTimeoutException.class)
    public Object StException() {
        return Re.SetStatus("获取数据超时！请重新载入");
    }


    @ExceptionCatch
    @ExceptionHandler(NullPointerException.class)
    public Object stException(){
        return Re.SetStatus("空值异常！");
    }

    @ExceptionCatch
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(ExpiredJwtException.class)
    public Object StException(ExpiredJwtException e){
        return Re.SetStatus("用户认证超时");
    }



    public Object StException(WxErrorException e){
        return Re.SetStatus(500,"Error","致命错误,请联系开发者！");
    }

    @ExceptionHandler(Re.class)
    public Object StException(Re e){
        return Re.SetStatus(e.getMessage());
    }
}

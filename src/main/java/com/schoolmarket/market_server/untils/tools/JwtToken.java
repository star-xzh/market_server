package com.schoolmarket.market_server.untils.tools;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.UUID;

public class JwtToken {
    private static final long time = 1000*60 * 60 * 24;
    private static final String encryKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1k+MLO8rjCFM5kcpvHpGhczuu+OCZNJUN0l3p1+majvKvVxnLLwMvQmYsH4cqQyPOk1hLrPaHmin7K6wIe+lCXNmcwaGQQSSYk02SpXTWAUEqA7Tq2TBKDCfg6De3xAe28l8CnEj+SS7Frar40M32CCHgLAGLbEwr9R6xqh8l1IeqKKJACTKV7PD9Z7z8TzFXRKzc/+eezGo8E+26Re16yxtej8A3nAnPojaSWcNEXK/kEZuYQ/nZ/MDOKn61PxS2xggJ1ulCYgH2JVZfiuC2KS0GSNM0qYmSxchaptETZvB1rgxgUwwnhiB2X2DUSlnoEJ/UiYc8IN1yZqQ+hEnRQIDAQAB";

    //创建JWToken
    public static String createToken(String openid,String role,String uuid){
        JwtBuilder jwtBuilder = Jwts.builder();
        return jwtBuilder
                //header
                .setHeaderParam("typ","JWT")
                .setHeaderParam("alg","HS256")
                //payload
                .claim("openid",openid)
                .claim("role",role)
                .claim("uuid",uuid)
                .setExpiration(new Date(System.currentTimeMillis()+time))
                .setId(UUID.randomUUID().toString())
                .setSubject("XZH")
                .signWith(SignatureAlgorithm.HS256,encryKey)
                .compact();
    }

    //校验token，布尔类型
    public static boolean checkToken(String jwtToken) {
        if(StringUtils.isEmpty(jwtToken)) return false;
        try {
            Jwts.parser().setSigningKey(encryKey).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            throw e; // 抛出异常交给拦截器处理
        }
        return true;
    }

    //获取jwt的openid
    public static String getUserOpenidByJwtToken(HttpServletRequest request){
        String jwtToken = request.getHeader("authorization");
        if (StringUtils.isEmpty(jwtToken)) return "";
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(encryKey).parseClaimsJws(jwtToken);
        Claims claims = claimsJws.getBody();
        return (String) claims.get("openid");
    }

    //获取jwt的uuid
    public static String getUserUUIDByJwtToken(HttpServletRequest request){
        String jwtToken = request.getHeader("authorization");
        if (StringUtils.isEmpty(jwtToken)) return "";
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(encryKey).parseClaimsJws(jwtToken);
        Claims claims = claimsJws.getBody();
        return (String) claims.get("uuid");
    }


}

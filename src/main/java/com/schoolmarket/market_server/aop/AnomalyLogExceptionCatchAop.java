package com.schoolmarket.market_server.aop;

import com.schoolmarket.market_server.untils.annotations.ExceptionCatch;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;

import java.util.Date;

public class AnomalyLogExceptionCatchAop {
    private static final Logger logger = Logger.getLogger(AnomalyLogExceptionCatchAop.class);


    @Around("@annotation(exceptionCatch)")
    public Object around(ProceedingJoinPoint joinPoint, ExceptionCatch exceptionCatch) throws Throwable{
        Object res;
        Object[] objects = joinPoint.getArgs();
        StringBuilder args = new StringBuilder();
        for (Object o: objects) {
            assert false;
            args.append(o).append(",");
        }
        logger.error("############### Anomaly Logger ###################");
        logger.warn("异常记录时间:" + new Date());
        logger.warn("异常方法名:" + joinPoint.getSignature().getName());
        logger.warn("异常参数:" + args);
        logger.error("############### 记录结束 ###################");
        res = joinPoint.proceed();

        return res;
    }
}

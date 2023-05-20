package com.schoolmarket.market_server.untils.annotations;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExceptionCatch {
    String value() default "";
}

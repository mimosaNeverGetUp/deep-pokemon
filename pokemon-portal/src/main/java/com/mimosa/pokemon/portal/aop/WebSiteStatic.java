package com.mimosa.pokemon.portal.aop;


import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class WebSiteStatic {

    @Pointcut("execution(public * com.mimosa.pokemon.portal.controller.*.*(..))")
    public void controllerAspect() {

    }

    @Pointcut("execution(public * com.mimosa.pokemon.portal.controller.IconController.*(..))")
    public void controllerExcept() {

    }
    //@Before("controllerAspect() &&!controllerExcept()")
    public void doBeforeAccess() {
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ip = httpServletRequest.getRemoteAddr();
    }
}

package com.mimosa.pokemon.portal.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @program: deep-pokemon
 * @description: gobal exception handler controller
 * @author: mimosa
 * @create: 2020//10//09
 */

@ControllerAdvice
public class ExceptionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionController.class);

    @ExceptionHandler({Exception.class})
    public String exceptionHandle(Exception e) {
        LOGGER.error("error",e);
        return "error";
    }
}

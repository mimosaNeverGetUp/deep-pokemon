///*
// * The MIT License
// *
// * Copyright (c) [2023]
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
// */

package com.mimosa.pokemon.portal.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class WebApplicationGlobalAdvice {
    @Autowired
    private Environment environment;

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    @ModelAttribute("serverPort")
    public String serverPort(final HttpServletRequest request) {
        return environment.getProperty("local.server.port");
    }

    @ModelAttribute("contextPath")
    public String contextPath(final HttpServletRequest request) {
        return request.getContextPath();
    }

    @ModelAttribute("requestScheme")
    public String requestScheme(final HttpServletRequest request) {
        return request.getScheme();
    }

    @ModelAttribute("requestServerName")
    public String requestServerName(final HttpServletRequest request) {
        return request.getServerName();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionController.class);

    @ExceptionHandler({Exception.class})
    public ModelAndView commonExceptionHandleWithModel(HttpServletRequest httpServletRequest, Exception e) {
        LOGGER.error("error occur", e);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error");
        fillModelGlobalParam(httpServletRequest, modelAndView);
        modelAndView.addObject("msg", e.getLocalizedMessage());
        if (e instanceof ErrorResponse errorResponse) {
            modelAndView.addObject("status", errorResponse.getStatusCode().value());
        }
        return modelAndView;
    }

    private void fillModelGlobalParam(HttpServletRequest request, ModelAndView modelAndView) {
        modelAndView.addObject("requestURI", requestURI(request));
        modelAndView.addObject("requestScheme", requestScheme(request));
        modelAndView.addObject("requestServerName", requestServerName(request));
        modelAndView.addObject("serverPort", serverPort(request));
        modelAndView.addObject("contextPath", contextPath(request));
    }
}

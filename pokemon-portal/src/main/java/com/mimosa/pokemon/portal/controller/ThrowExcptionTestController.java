package com.mimosa.pokemon.portal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @program: deep-pokemon
 * @description: test
 * @author: mimosa
 * @create: 2020//10//09
 */

@Controller
public class ThrowExcptionTestController {
    @RequestMapping("/throw")
    public String throwE() throws Exception {
        throw new Exception("test");

    }
}

/*
 * The MIT License
 *
 * Copyright (c) [2022] [Xiaocong Huang]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.mimosa.pokemon.portal.controller;

import com.mimosa.pokemon.portal.dto.PokemonStatDto;
import com.mimosa.pokemon.portal.service.BattleService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class PokemonStatisticController {

    @Autowired
    BattleService battleService;


    @RequestMapping(value = "/result", method = RequestMethod.POST)
    public String statAll(Model model , HttpServletRequest request) throws Exception {
        String dayAfterString = request.getParameter("dayAfter");
        String dayBeforeString = request.getParameter("dayBefore");
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dayAfter = LocalDate.parse(dayAfterString, format);
        LocalDate dayBefore = LocalDate.parse(dayBeforeString, format);
        //提取对比统计日期，若没有自定义初始化
        String dayBeforeString_compare;
        String dayAfterString_compare;
        LocalDate dayAfter_compare;
        LocalDate dayBefore_compare;
        dayBeforeString_compare = request.getParameter("dayBefore_compare");
        dayAfterString_compare = request.getParameter("dayAfter_compare");
        if (StringUtils.isEmpty(dayAfterString_compare) || StringUtils.isEmpty(dayBeforeString_compare)) {
            dayAfter_compare = dayAfter.minusDays(15);
            dayBefore_compare = dayAfter;
        } else {
            dayAfter_compare = LocalDate.parse(dayAfterString_compare, format);
            dayBefore_compare = LocalDate.parse(dayBeforeString_compare, format);

        }

        List<PokemonStatDto> pokemonStatDtos = battleService.queryPokemonStat(dayAfter, dayBefore);
        List<PokemonStatDto> pokemonStatDtosCompare = battleService.queryPokemonStat(dayAfter_compare, dayBefore_compare);
        model.addAttribute("stat", pokemonStatDtos);
        model.addAttribute("compareStat",
                pokemonStatDtosCompare.stream().collect(Collectors.groupingBy(PokemonStatDto::getName)));
        return "statResult";
    }


    @RequestMapping(value = "/stat")
    public String input() {
        return "statForm";
    }
}
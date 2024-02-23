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

import com.mimosa.deeppokemon.entity.Team;
import com.mimosa.pokemon.portal.service.BattleService;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class TeamListController {
    @Autowired
    private BattleService battleService;

    @RequestMapping(value = "/team", method = RequestMethod.GET)
    public String TeamListForm(Model model, HttpServletRequest request) {
        return "teamForm";

    }

    @RequestMapping(value = "/teamList", method = RequestMethod.GET)
    public String TeamList(Model model, HttpServletRequest request) {
        //提取参数
        int page = Integer.parseInt(request.getParameter("page"));
        String dayBefore = request.getParameter("dayBefore");
        String dayAfter = request.getParameter("dayAftter");
        String tag = request.getParameter("tag");
        String pokemonName = request.getParameter("pokemonName");
        List<Pair<Team, String>> list = battleService.Team(page, tag, pokemonName, dayAfter, dayBefore);
        //传入上一页和下一页需要的查询参数语句
        String originQuery = "page=" + String.valueOf(page);
        String nextPageQuery = "page=" + String.valueOf(page + 1);
        String previousPageQuery = "page=" + String.valueOf(page - 1);
        String nextQueryString = "?" + request.getQueryString().replace(originQuery, nextPageQuery);
        String previousQueryString = "?" + request.getQueryString().replace(originQuery, previousPageQuery);
        model.addAttribute("nextQueryString", nextQueryString);
        model.addAttribute("previousQueryString", previousQueryString);
        model.addAttribute("pairList", list);
        model.addAttribute("page", page);
        return "teamList";

    }
}

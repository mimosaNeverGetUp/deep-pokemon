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

    @RequestMapping(value = "/team",method = RequestMethod.GET)
    public String TeamListForm(Model model, HttpServletRequest request) {
        return "teamForm";

    }

    @RequestMapping(value = "/teamList",method = RequestMethod.GET)
    public String TeamList(Model model, HttpServletRequest request) {
        //提取参数
        int page = Integer.parseInt(request.getParameter("page"));
        String dayBefore = request.getParameter("dayBefore");
        String dayAfter = request.getParameter("dayAftter");
        String tag = request.getParameter("tag");
        String pokemonName = request.getParameter("pokemonName");
        List<Pair<Team, String>> list = battleService.Team1( page,tag,pokemonName,dayAfter,dayBefore);
        //传入上一页和下一页需要的查询参数语句
        String originQuery = "page=" + String.valueOf(page);
        String nextPageQuery = "page=" + String.valueOf(page+1);
        String previousPageQuery = "page=" + String.valueOf(page-1);
        String nextQueryString = "?"+request.getQueryString().replace(originQuery, nextPageQuery);
        String previousQueryString = "?"+request.getQueryString().replace(originQuery, previousPageQuery);
        model.addAttribute("nextQueryString", nextQueryString);
        model.addAttribute("previousQueryString", previousQueryString);
        model.addAttribute("pairList", list);
        model.addAttribute("page", page);
        return "teamList";

    }
}

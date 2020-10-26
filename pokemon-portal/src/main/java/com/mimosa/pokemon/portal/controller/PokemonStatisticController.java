package com.mimosa.pokemon.portal.controller;

import com.mimosa.deeppokemon.entity.Team;
import com.mimosa.pokemon.portal.entity.MapResult;
import com.mimosa.pokemon.portal.service.BattleService;
import com.mimosa.pokemon.portal.util.MapResultUtil;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class PokemonStatisticController {

    @Autowired
    BattleService battleService;


    @RequestMapping(value = "/result", method = RequestMethod.POST)
    public String statAll(Model model , HttpServletRequest request) throws Exception {
        String name = request.getParameter("name");
        String dayA = request.getParameter("dayAfter");
        String dayB = request.getParameter("dayBefore");
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dayAfter = LocalDate.parse(dayA, format);
        LocalDate dayBefore = LocalDate.parse(dayB, format);
        List<MapResult> mapResultList = battleService.statisticAllDetails(name, dayAfter, dayBefore);
        List<MapResult> mapResultListCompare = battleService.statisticAll(name, dayAfter.minusDays(15), dayAfter);
        MapResultUtil.compareStatistic(mapResultList, mapResultListCompare);//这里向类注入了胜率差、等差率差
        model.addAttribute("list", mapResultList);
        return "statResult";
    }


    @RequestMapping(value = "/stat")
    public String input() {
        return "statForm";
    }
}

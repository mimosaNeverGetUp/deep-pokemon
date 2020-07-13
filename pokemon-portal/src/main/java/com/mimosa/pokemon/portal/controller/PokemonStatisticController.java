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
    public String stat(Model model , HttpServletRequest request) {
        String name = request.getParameter("name");
        String dayA = request.getParameter("dayAfter");
        String dayB = request.getParameter("dayBefore");
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dayAfter = LocalDate.parse(dayA, format);
        LocalDate dayBefore = LocalDate.parse(dayB, format);
        Pair<Pair<Float, Float>, List<Team>> pair = battleService.statistic(name, dayAfter, dayBefore);
        model.addAttribute("pair", pair);
        model.addAttribute("name", name);
        return "statResult";
    }

    @RequestMapping(value = "/stat")
    public String input() {
        return "statForm";
    }
}

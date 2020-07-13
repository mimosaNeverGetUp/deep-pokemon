package com.mimosa.pokemon.portal.controller;

import com.mimosa.deeppokemon.entity.Team;
import com.mimosa.pokemon.portal.service.BattleService;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class TeamListController {
    @Autowired
    private BattleService battleService;

    @RequestMapping("/team")
    public String TeamList(Model model, int page) {
        List<Pair<Team, String>> list = battleService.Team( page);
        model.addAttribute("pairList", list);
        model.addAttribute("page", page);
        return "teamList";

    }
}

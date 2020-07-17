package com.mimosa.pokemon.portal.controller;

import com.mimosa.deeppokemon.entity.Player;
import com.mimosa.deeppokemon.entity.Team;
import com.mimosa.pokemon.portal.service.BattleService;
import com.mimosa.pokemon.portal.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class PlayerListController {
    @Autowired
    private BattleService battleService;

    @Autowired
    private PlayerService playerService;

    @RequestMapping("/list")
    public String list(Model model) {
        return "redirect:/rank?page=1";
    }

    @RequestMapping("/rank")
    public String list(Model model,int page) {
        List<Player> playerList = playerService.listPlayerRank(page);
        List<Team> teamList = battleService.listTeamByPlayerList(playerList);
        model.addAttribute("playerList", playerList);
        model.addAttribute("teamList", teamList);
        model.addAttribute("page", page);
        return "playerRank";
    }

}

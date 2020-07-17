package com.mimosa.pokemon.portal.controller;

import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.Player;
import com.mimosa.pokemon.portal.service.BattleService;
import com.mimosa.pokemon.portal.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class PlayerController {
    @Autowired
    private BattleService battleService;

    @Autowired
    private PlayerService playerService;

    @RequestMapping("/record")
    public String player(String name, Model model,int page) {
        List<Battle> battleList = battleService.listBattleByName(name,page);
        Player player = playerService.findPlayerByName(name);
        if (player == null) {
            player = new Player();
            player.setName(name);
        }
        model.addAttribute("player", player);
        model.addAttribute("battleList", battleList);
        model.addAttribute("page", page);
        return "playerRecord";
    }

    @RequestMapping("/test")
    public ModelAndView test() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("fuck.html");
        return modelAndView;
    }


}

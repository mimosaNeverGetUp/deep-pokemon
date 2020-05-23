package com.mimosa.deeppokemon.crawler;

import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.Team;

public interface TeamCrawler {
    Battle craw(String url);
}

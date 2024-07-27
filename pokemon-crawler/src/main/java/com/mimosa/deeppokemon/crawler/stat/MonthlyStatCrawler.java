/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.crawler.stat;

import com.mimosa.deeppokemon.crawler.stat.dto.MonthlyBattleStatDto;
import com.mimosa.deeppokemon.utils.HttpProxy;
import org.apache.hc.core5.net.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerErrorException;

import java.net.URISyntaxException;

@Component
public class MonthlyStatCrawler {
    private static final Logger log = LoggerFactory.getLogger(MonthlyStatCrawler.class);
    private static final String SMOGON_STAT_BASE_URL = "https://pkmn.github.io/smogon/data/stats/";
    protected static final String PATTERN_FORMAT_TXT = "%s.json";

    private final HttpProxy httpProxy;

    public MonthlyStatCrawler(HttpProxy httpProxy) {
        this.httpProxy = httpProxy;
    }

    @RegisterReflectionForBinding(MonthlyBattleStatDto.class)
    public MonthlyBattleStatDto craw(String format) {
        try {
            String url = initStatQuery(format);
            log.debug("stat craw request uri: {}", url);
            return httpProxy.get(url, MonthlyBattleStatDto.class);
        } catch (URISyntaxException e) {
            throw new ServerErrorException(e.getLocalizedMessage(), e);
        }
    }

    private String initStatQuery(String format) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(SMOGON_STAT_BASE_URL);
        uriBuilder.appendPath(String.format(PATTERN_FORMAT_TXT, format));
        return uriBuilder.build().toString();
    }
}
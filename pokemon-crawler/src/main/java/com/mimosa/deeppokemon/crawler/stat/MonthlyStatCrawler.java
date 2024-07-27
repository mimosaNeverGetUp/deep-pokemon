/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.crawler.stat;

import com.mimosa.deeppokemon.crawler.stat.dto.MonthlyBattleStatDto;
import com.mimosa.deeppokemon.utils.HttpUtil;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
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

    @RegisterReflectionForBinding(MonthlyBattleStatDto.class)
    public MonthlyBattleStatDto craw(String format) {
        try {
            ClassicHttpRequest usageQueryRequest = initStatQuery(format);
            log.debug("stat craw request uri: {}", usageQueryRequest.getRequestUri());
            return HttpUtil.request(usageQueryRequest, MonthlyBattleStatDto.class);
        } catch (URISyntaxException e) {
            throw new ServerErrorException(e.getLocalizedMessage(), e);
        }
    }

    private ClassicHttpRequest initStatQuery(String format) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(SMOGON_STAT_BASE_URL);
        uriBuilder.appendPath(String.format(PATTERN_FORMAT_TXT, format));
        return ClassicRequestBuilder.get(uriBuilder.build()).build();
    }
}
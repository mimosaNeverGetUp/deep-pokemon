/*
 *  MIT License
 *
 *  Copyright (c) 2026-2026 mimosa
 */

package com.mimosa.deeppokemon.crawler.stat;

import com.mimosa.deeppokemon.crawler.stat.dto.*;
import com.mimosa.deeppokemon.crawler.stat.dto.smogon.SmogonMonthlyPokemonStatDto;
import com.mimosa.deeppokemon.crawler.stat.dto.smogon.SmogonMonthlyStatDto;
import com.mimosa.deeppokemon.info.AbilityInfoProvider;
import com.mimosa.deeppokemon.info.ItemInfoProvider;
import com.mimosa.deeppokemon.info.MoveInfoProvider;
import com.mimosa.deeppokemon.utils.HttpProxy;
import org.apache.commons.lang.StringUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerErrorException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SmogonMonthlyStatCrawler {
    private final static Logger log = LoggerFactory.getLogger(SmogonMonthlyStatCrawler.class);
    private static final String SMOGON_STAT_BASE_URL = "https://www.smogon.com/stats/";
    private static final String SMOGON_STAT_CHAOS_PATH = "chaos";
    private static final String SMOGON_STAT_METAGAME_PATH = "metagame";
    private static final String SMOGON_STAT_CUTOFF_1695 = "1695";
    private static final String SMOGON_STAT_CUTOFF_1630 = "1630";
    protected static final String PATTERN_FORMAT_JSON = "%s-%s.json";
    protected static final String PATTERN_FORMAT_TXT = "%s-%s.txt";
    protected static final String PATTERN_PATH_DATE = "%s-%s";
    protected static final String GEN_9_OU = "gen9ou";
    protected static final String GEN_9_DOUBLESOU = "gen9doublesou";

    protected static final Pattern METAGAME_TAG_PATTERN = Pattern.compile("([^.]+)\\.{3,}([^%]+)");
    protected static final int META_TAG_INDEX = 1;
    protected static final int META_USAGE_INDEX = 2;
    protected static final int USAGE_SCALE = 4;
    protected static final BigDecimal PERCENT_DIVISOR = new BigDecimal("100");


    private final HttpProxy httpProxy;
    private final ItemInfoProvider itemInfoProvider;
    private final MoveInfoProvider moveInfoProvider;
    private final AbilityInfoProvider abilityInfoProvider;

    public SmogonMonthlyStatCrawler(HttpProxy httpProxy, ItemInfoProvider itemInfoProvider, MoveInfoProvider moveInfoProvider, AbilityInfoProvider abilityInfoProvider) {
        this.httpProxy = httpProxy;
        this.itemInfoProvider = itemInfoProvider;
        this.moveInfoProvider = moveInfoProvider;
        this.abilityInfoProvider = abilityInfoProvider;
    }

    @RegisterReflectionForBinding(SmogonMonthlyStatDto.class)
    public MonthlyBattleStatDto craw(String format, String statId) {
        try {
            String url = initChaosStatQuery(format, statId);
            log.debug("stat craw request uri: {}", url);
            SmogonMonthlyStatDto smogonMonthlyStatDto = httpProxy.get(url, SmogonMonthlyStatDto.class);
            MonthlyMetaGameStatDto monthlyMetaGameStatDto = crawSmogonMetagame(format, statId);
            return parseSmognMonthlyStat(statId, smogonMonthlyStatDto, monthlyMetaGameStatDto);
        } catch (URISyntaxException e) {
            throw new ServerErrorException(e.getLocalizedMessage(), e);
        }
    }

    private MonthlyBattleStatDto parseSmognMonthlyStat(String statId, SmogonMonthlyStatDto smogonMonthlyStatDto,
                                                       MonthlyMetaGameStatDto monthlyMetaGameStatDto) {
        LocalDate localDate = LocalDate.of(Integer.parseInt(statId.substring(0, 4)), Integer.parseInt(statId.substring(4, 6)), 1);
        int battles = smogonMonthlyStatDto.info().battleCount();
        Map<String, MonthlyPokemonStatDto> pokemon = new HashMap<>();
        for (var entry : smogonMonthlyStatDto.pokemon().entrySet()) {
            pokemon.put(entry.getKey(), parseMonthlyPokemonStatDto(entry.getValue()));
        }
        return new MonthlyBattleStatDto(localDate, battles, pokemon, monthlyMetaGameStatDto);
    }

    private String initChaosStatQuery(String format, String statId) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(SMOGON_STAT_BASE_URL);
        // if id is 202605gen9ou, smogon path is 2026-05
        uriBuilder.appendPath(String.format(PATTERN_PATH_DATE, statId.substring(0, 4), statId.substring(4,
                6)));
        uriBuilder.appendPath(SMOGON_STAT_CHAOS_PATH);
        if (StringUtils.equals(format, GEN_9_OU) || StringUtils.equals(format, GEN_9_DOUBLESOU)) {
            uriBuilder.appendPath(String.format(PATTERN_FORMAT_JSON, format, SMOGON_STAT_CUTOFF_1695));
        } else {
            uriBuilder.appendPath(String.format(PATTERN_FORMAT_JSON, format, SMOGON_STAT_CUTOFF_1630));
        }
        return uriBuilder.build().toString();
    }

    private MonthlyMetaGameStatDto crawSmogonMetagame(String format, String statId) throws URISyntaxException {
        URIBuilder uriBuilder = getUriBuilder(format, statId);
        String content = httpProxy.get(uriBuilder.build().toString());

        LinkedHashMap<String, Double> tags = new LinkedHashMap<>();
        content.lines().forEach(line -> {
            Matcher matcher = METAGAME_TAG_PATTERN.matcher(line);
            if (matcher.find()) {
                try {
                    String teamTag = matcher.group(META_TAG_INDEX).trim();

                    BigDecimal usage = new BigDecimal(matcher.group(META_USAGE_INDEX).trim())
                            .divide(PERCENT_DIVISOR, USAGE_SCALE, RoundingMode.HALF_UP);
                    if (usage.compareTo(BigDecimal.ZERO) > 0) {
                        tags.put(teamTag, usage.doubleValue());
                    }
                } catch (Exception e) {
                    log.error("can not parse {}", matcher.group());
                }
            }
        });

        if (tags.isEmpty()) {
            log.warn("parse empty metagame, format: {} statId: {}", format, statId);
        }
        return new MonthlyMetaGameStatDto(tags);
    }

    private static @NotNull URIBuilder getUriBuilder(String format, String statId) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(SMOGON_STAT_BASE_URL);
        // if id is 202605gen9ou, smogon path is 2026-05
        uriBuilder.appendPath(String.format(PATTERN_PATH_DATE, statId.substring(0, 4), statId.substring(4, 6)));
        uriBuilder.appendPath(SMOGON_STAT_METAGAME_PATH);
        if (StringUtils.equals(format, GEN_9_OU) || StringUtils.equals(format, GEN_9_DOUBLESOU)) {
            uriBuilder.appendPath(String.format(PATTERN_FORMAT_TXT, format, SMOGON_STAT_CUTOFF_1695));
        } else {
            uriBuilder.appendPath(String.format(PATTERN_FORMAT_TXT, format, SMOGON_STAT_CUTOFF_1630));
        }
        return uriBuilder;
    }

    private MonthlyPokemonStatDto parseMonthlyPokemonStatDto(SmogonMonthlyPokemonStatDto smogonMonthlyPokemonStatDto) {
        LeadStat lead = null;
        Usage usage = new Usage(0D, 0D, getIEEE754Value(smogonMonthlyPokemonStatDto.usage()));
        int count = smogonMonthlyPokemonStatDto.rawCount();
        double weight = 0D;
        BigDecimal weightCount = getPokemonWeightCount(smogonMonthlyPokemonStatDto);
        LinkedHashMap<String, Double> abilities = countUsage(parseDoubleMap(smogonMonthlyPokemonStatDto.abilities()), weightCount,
                abilityInfoProvider::getName);
        LinkedHashMap<String, Double> items = countUsage(smogonMonthlyPokemonStatDto.items(), weightCount,
                itemInfoProvider::getName);
        LinkedHashMap<String, Double> moves = countUsage(smogonMonthlyPokemonStatDto.moves(), weightCount,
                moveInfoProvider::getName);
        LinkedHashMap<String, Double> teraTypes = countUsage(smogonMonthlyPokemonStatDto.teraTypes(), weightCount,
                StringUtils::capitalize);
        LinkedHashMap<String, Double> teammates = countUsage(smogonMonthlyPokemonStatDto.teammates(), weightCount,
                StringUtils::capitalize);
        LinkedHashMap<String, Double> happiness = countUsage(smogonMonthlyPokemonStatDto.happinesses(),
                weightCount, null);
        LinkedHashMap<String, Double> spreads = countUsage(smogonMonthlyPokemonStatDto.spreads(), weightCount, null);

        return new MonthlyPokemonStatDto(lead, usage, count, weight, Collections.emptyList(), abilities,
                items, spreads, moves, teraTypes, teammates, happiness, new LinkedHashMap<>());
    }

    private Map<String, Double> parseDoubleMap(Map<String, BigDecimal> abilities) {
        Map<String, Double> doubleMap = new HashMap<>();
        for (var entry : abilities.entrySet()) {
            doubleMap.put(entry.getKey(), entry.getValue().doubleValue());
        }
        return doubleMap;
    }

    private BigDecimal getPokemonWeightCount(SmogonMonthlyPokemonStatDto smogonMonthlyPokemonStatDto) {
        return smogonMonthlyPokemonStatDto.abilities().values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private LinkedHashMap<String, Double> countUsage(Map<String, Double> map, BigDecimal weightCount,
                                                     UnaryOperator<String> keyMapFunction) {
        LinkedHashMap<String, Double> usageMap = new LinkedHashMap<>();
        double weightCountValue = weightCount.doubleValue();

        for (var entry : map.entrySet()) {
            String key = entry.getKey();
            double count = entry.getValue();
            double usage = count / weightCountValue;

            usage = getIEEE754Value(usage);
            if (usage > 0) {
                usageMap.put((keyMapFunction == null || keyMapFunction.apply(key) == null) ?
                        key : keyMapFunction.apply(key), usage);
            }
        }

        // avoid memory exhaust
        map.clear();
        List<Map.Entry<String, Double>> list =
                new ArrayList<>(usageMap.entrySet());
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        LinkedHashMap<String, Double> sortedMapDesc =
                new LinkedHashMap<>(usageMap.size());
        for (var e : list) {
            sortedMapDesc.put(e.getKey(), e.getValue());
        }

        usageMap.clear();
        list.clear();
        return sortedMapDesc;
    }

    private static double getIEEE754Value(double usage) {
        // 小数点后四位
        return Math.round(usage * 10000d) / 10000d;
    }
}
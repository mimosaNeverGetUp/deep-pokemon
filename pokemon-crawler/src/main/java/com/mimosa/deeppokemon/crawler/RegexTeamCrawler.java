package com.mimosa.deeppokemon.crawler;

import com.mimosa.deeppokemon.entity.Battle;
import com.mimosa.deeppokemon.entity.Team;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class RegexTeamCrawler implements TeamCrawler {
    private static Logger log = LoggerFactory.getLogger(RegexTeamCrawler.class);
    public RegexTeamCrawler() {
    }

    @Override
    public Battle craw(String url) {
        HttpGet httpGet = initGet(url);
        try (CloseableHttpClient httpClient = initClient();CloseableHttpResponse HttpResponse=httpClient.execute(httpGet);){
            String html = EntityUtils.toString(HttpResponse.getEntity());
            Battle battle = HtmlTeamExtracter.extract(html);
            String battleID = extractBattleID(url);
            battle.setBattleID(battleID);
            return battle;
        }
        catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return null;
    }

    private CloseableHttpClient initClient(){
        CookieStore httpCookieStore = new BasicCookieStore();
        CloseableHttpClient httpClient= HttpClientBuilder.create().setDefaultCookieStore(httpCookieStore).build();
        return httpClient;
    }

    private HttpGet initGet(String url){
        HttpGet httpGet=new HttpGet(url);
        httpGet.addHeader("Accept", "*/*");
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Mobile Safari/537.36");
        RequestConfig config= RequestConfig.custom().setConnectTimeout(20000).//创建连接的最长时间，单位是毫秒
                setConnectionRequestTimeout(20000).//设置获取连接的最长时间，单位毫秒
                setSocketTimeout(20000)//设置数据传输的最长时间，单位毫秒
                .build();
        httpGet.setConfig(config);
        return  httpGet;
    }

    public static String extractBattleID (String url) throws Exception{
        Pattern pattern = Pattern.compile("https://replay.pokemonshowdown.com/(.*)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            String battleID = matcher.group(1).trim();
            return battleID;
        }
        throw new Exception("match battle id failed");
    }
}

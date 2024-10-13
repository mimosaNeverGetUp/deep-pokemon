/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.deeppokemon.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mimosa.deeppokemon.utils.HttpUtil;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerErrorException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class AiService {
    protected static final String URL_API_GPT_V_1_CHAT_COMPLETIONS = "https://api.chatanywhere.cn/v1/chat/completions";
    protected static final String GPT_4_O_MINI = "gpt-4o-mini";
    protected static final String MESSAGES = "messages";
    protected static final String MODEL = "model";
    protected static final String AUTHORIZATION = "Authorization";
    protected static final String USER_AGENT = "User-Agent";
    protected static final String CONTENT_TYPE = "Content-Type";
    protected static final String SYSTEM = "system";
    protected static final String USER = "user";

    @Value("${OPENAPI_SK:}")
    private String openApiSk;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @RegisterReflectionForBinding(value = {ChatResponse.class, ChatMessage.class, ChatChoice.class})
    public String translate(String text, String prompt) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put(MODEL, GPT_4_O_MINI);
        params.put(MESSAGES, buildMessages(text, prompt));
        RequestConfig requestConfig = RequestConfig.custom()
                .setResponseTimeout(60, TimeUnit.SECONDS)   // 设置连接超时，单位为毫秒
                .setConnectionRequestTimeout(60, TimeUnit.SECONDS)
                .build();

        String data = OBJECT_MAPPER.writeValueAsString(params);
        HttpPost post = new HttpPost(URL_API_GPT_V_1_CHAT_COMPLETIONS);
        post.setHeader(AUTHORIZATION, openApiSk);
        post.setHeader(USER_AGENT, "Apifox/1.0.0 (https://apifox.com)");
        post.setHeader(CONTENT_TYPE, "application/json");
        post.setEntity(new StringEntity(data, StandardCharsets.UTF_8));
        post.setConfig(requestConfig);

        ChatResponse chatResponse = HttpUtil.request(post, ChatResponse.class);
        if (chatResponse.choices == null || chatResponse.choices().isEmpty()) {
            throw new ServerErrorException("chat api response invalid", null);
        }

        return chatResponse.choices.get(0).message().content();
    }

    private List<ChatMessage> buildMessages(String text, String prompt) {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage(SYSTEM, prompt));
        messages.add(new ChatMessage(USER, text));
        return messages;
    }

    private record ChatMessage(String role, String content) {

    }

    private record ChatChoice(ChatMessage message) {

    }

    public record ChatResponse(List<ChatChoice> choices) {

    }
}
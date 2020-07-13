package com.mimosa.pokemon.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication

public class PokemonPortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(PokemonPortalApplication.class, args);
    }

}

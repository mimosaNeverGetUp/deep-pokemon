/*
 * The MIT License
 *
 * Copyright (c) [2022] [Xiaocong Huang]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.mimosa.deeppokemon.crawler;

import com.alibaba.fastjson2.JSONObject;
import com.mimosa.deeppokemon.entity.PokemonInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PokemonIconExtracter {
    private static final Logger logger = LoggerFactory.getLogger(PokemonIconExtracter.class);

    final private static int HEIGHT_PER_POKEMON = 30;

    final private static int WIDTH_PER_POKEMON = 40;

    final private Map<String, Integer> pokemonIconIndexMap;

    final private Path imagePath;

    List<PokemonInfo> pokemonInfos;

    PokemonIconExtracter(Path imagePath, Path pokemonIconIndexPath, List<PokemonInfo> pokemonInfos) throws IOException {
        this.imagePath = imagePath;
        this.pokemonInfos = pokemonInfos;
        pokemonIconIndexMap = getPokemonIconIndexMap(pokemonIconIndexPath);
    }

    public void extract() throws IOException {
        ImageIcon pokemonicons = new ImageIcon(imagePath.toAbsolutePath().toString());
        BufferedImage pokemonIconsImage = new BufferedImage(pokemonicons.getIconWidth(), pokemonicons.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB);
        pokemonIconsImage.getGraphics().drawImage(pokemonicons.getImage(), 0, 0, null);
        pokemonicons.getImage().flush();

        for (PokemonInfo pokemonInfo : pokemonInfos) {
            try {
                int index = getPokemonIconIndex(pokemonInfo);
                if (index < 0) {
                    continue;
                }
                int h = index / 12 * 30;
                int w = (index % 12) * 40;
                BufferedImage pokemonIconImage = pokemonIconsImage.getSubimage(w, h, WIDTH_PER_POKEMON, HEIGHT_PER_POKEMON);
                File output = Paths.get("src", "main", "resources", "META-INF", "test", pokemonInfo.getName()
                        .replaceAll(" ", "").replaceAll(":", "") + ".png").toAbsolutePath().toFile();
                ImageIO.write(convertBackgroundToCompatible(pokemonIconImage), "png", output);
            } catch (Exception e) {
                logger.error("extract {} fail,index:{}", pokemonInfo, getPokemonIconIndex(pokemonInfo));
                throw e;
            }
        }
    }

    private int getPokemonIconIndex(PokemonInfo pokemonInfo) {
        if (pokemonIconIndexMap.containsKey(pokemonInfo.getId())) {
            return pokemonIconIndexMap.get(pokemonInfo.getId());
        }

        return pokemonInfo.getNumber();
    }

    private Map<String, Integer> getPokemonIconIndexMap(Path pokemonIconIndexPath) throws IOException {
        String content = new String(Files.readAllBytes(pokemonIconIndexPath));
        return jsonToMap(content);
    }

    /**
     * convert json string to map
     */
    private Map<String, Integer> jsonToMap(String json) {
        Map<String, Integer> map = new HashMap<>();
        JSONObject jsonObject = JSONObject.parseObject(json);
        Set<String> set = jsonObject.keySet();
        for (String key : set) {
            map.put(key, jsonObject.getInteger(key));
        }
        return map;
    }


    public static BufferedImage convertBackgroundToCompatible(Image image) {
        // Create new image with white background
        BufferedImage newImage = new BufferedImage(
                image.getWidth(null),
                image.getHeight(null),
                BufferedImage.TYPE_INT_RGB
        );
        Graphics2D g = newImage.createGraphics();
        newImage = g.getDeviceConfiguration().createCompatibleImage(image.getWidth(null), image.getHeight(null),
                Transparency.TRANSLUCENT);
        g = newImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }
}
package com.mimosa.deeppokemon.crawler;

import com.alibaba.fastjson2.JSONObject;
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
import java.util.Map;
import java.util.Set;

public class ItemIconExtracter {
    private static final Logger logger = LoggerFactory.getLogger(ItemIconExtracter.class);

    private static final int HEIGHT_PER_ICON = 24;

    private static final int WIDTH_PER_ICON = 24;

    private final Map<String, Integer> itemsIconIndexMap;

    private final Path imagePath;

    ItemIconExtracter(Path imagePath, Path itemIconIndexPath) throws IOException {
        this.imagePath = imagePath;
        itemsIconIndexMap = getItemIconIndexMap(itemIconIndexPath);
    }

    public void extract() throws IOException {
        ImageIcon pokemonicons = new ImageIcon(imagePath.toAbsolutePath().toString());
        BufferedImage pokemonIconsImage = new BufferedImage(pokemonicons.getIconWidth(), pokemonicons.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB);
        pokemonIconsImage.getGraphics().drawImage(pokemonicons.getImage(), 0, 0, null);
        pokemonicons.getImage().flush();

        for (Map.Entry<String, Integer> entry : itemsIconIndexMap.entrySet()) {
            try {
                String item = entry.getKey();
                int index = itemsIconIndexMap.get(item);
                if (index < 0) {
                    continue;
                }
                int h = index / 16 * HEIGHT_PER_ICON;
                int w = (index % 16) * WIDTH_PER_ICON;
                BufferedImage pokemonIconImage = pokemonIconsImage.getSubimage(w, h, WIDTH_PER_ICON, HEIGHT_PER_ICON);
                File output = Paths.get("src", "main", "resources", "pokemon", "test", item
                        .replace(":", "") + ".png").toAbsolutePath().toFile();
                ImageIO.write(convertBackgroundToCompatible(pokemonIconImage), "png", output);
            } catch (Exception e) {
                logger.error("extract {} fail,index:{}", entry.getKey(), itemsIconIndexMap.get(entry.getKey()));
                throw e;
            }
        }
    }

    private Map<String, Integer> getItemIconIndexMap(Path pokemonIconIndexPath) throws IOException {
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
            map.put(jsonObject.getJSONObject(key).getString("name"), jsonObject.getJSONObject(key).getInteger(
                    "spritenum"));
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
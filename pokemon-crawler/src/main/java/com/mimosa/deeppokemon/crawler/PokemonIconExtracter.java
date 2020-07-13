package com.mimosa.deeppokemon.crawler;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Scanner;

public class PokemonIconExtracter {
    private static int height_perPokemon = 30;
    private static int width_perPokemon = 40;
    public static void extract(String path) throws IOException {
        ImageInputStream iis = ImageIO.createImageInputStream(new FileInputStream(path));
        @SuppressWarnings("rawtypes")
        Iterator it = ImageIO.getImageReaders(iis);
        ImageReader imagereader = (ImageReader) it.next();
        imagereader.setInput(iis);
        ImageReadParam par = imagereader.getDefaultReadParam();
        InputStream inputStream = new FileInputStream("E:\\java\\study\\deep-pokemon\\pokemon-crawler\\src\\main\\resources\\META-INF\\pokemonicon\\pmname.txt");
        Scanner scanner = new Scanner(inputStream);
        int hNum = imagereader.getHeight(0) / height_perPokemon;
        System.out.println(hNum);
        int wNum = imagereader.getWidth(0) / width_perPokemon;
        System.out.println(wNum);
        int num = 0;
        String name = " ";
        for (int h = 0; h <hNum ; h++) {
            for (int w = 0; w < wNum; w++) {
                if (scanner.hasNext()) {
                    name = scanner.nextLine().trim();
                } else {
                    name = String.valueOf(num);
                }
                System.out.println(w);
                par.setSourceRegion(new Rectangle(w * width_perPokemon/2, h * height_perPokemon, width_perPokemon, height_perPokemon));
                BufferedImage bi = imagereader.read(0, par);
                ImageIO.write(bi, "png", new File("E:\\java\\study\\deep-pokemon\\pokemon-crawler\\src\\main\\resources\\META-INF\\test\\" + name + ".png"));
                ++num;
            }
        }
    }
}

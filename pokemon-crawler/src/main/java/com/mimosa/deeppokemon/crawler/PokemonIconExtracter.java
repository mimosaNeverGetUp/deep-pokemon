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

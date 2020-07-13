package com.mimosa.pokemon.portal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
public class IconController {

    @RequestMapping("/icon")
    public void icon(HttpServletResponse response, String name)  {
        if (name.contains("Silvally")) {
            name = "Silvally";
        }
        if (name.contains("Urshifu")) {
            name = "Urshifu";
        }
        if (name.contains("Gourgeist")) {
            name = "Gourgeist";
        }
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("templates/test/"+name + ".png");
//        File file = new File(path);
//        if(!file.exists()) {
//            return;
//        }
        try {
            //FileInputStream inputStream = new FileInputStream(file);
            byte[] data = toByteArray(inputStream);
            inputStream.close();
            response.setContentType("image/png");
            OutputStream os = response.getOutputStream();
            os.write(data);
            os.flush();
            os.close();
            return;
        } catch (IOException e) {

        }
    }

    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024*4];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }
}

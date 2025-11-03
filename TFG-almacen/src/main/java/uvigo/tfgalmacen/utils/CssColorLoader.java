package uvigo.tfgalmacen.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.*;

public class CssColorLoader {

    public static Map<String, String> loadColors(String resourcePath) {
        Map<String, String> colorMap = new HashMap<>();


        try (InputStream inputStream = CssColorLoader.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                System.err.println("⚠️ No se encontró el recurso: " + resourcePath);
                return colorMap;
            }


            String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            Pattern pattern = Pattern.compile("-([a-zA-Z0-9-_]+):\\s*([^;]+);");
            Matcher matcher = pattern.matcher(content);

            while (matcher.find()) {
                String colorName = "-" + matcher.group(1);
                String colorValue = matcher.group(2).trim();
                colorMap.put(colorName, colorValue);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return colorMap;
    }
}

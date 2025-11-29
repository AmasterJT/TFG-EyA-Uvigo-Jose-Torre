package uvigo.tfgalmacen.utils;

import uvigo.tfgalmacen.Main;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.*;

public class CssColorLoader {

    private static final Logger LOGGER = Logger.getLogger(CssColorLoader.class.getName());

    static {
        // Sube el nivel del logger
        LOGGER.setLevel(Level.ALL);

        // Evita que use los handlers del padre (que suelen estar en INFO con SimpleFormatter)
        LOGGER.setUseParentHandlers(false);

        // Crea un ConsoleHandler propio con tu ColorFormatter
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);                 // ¡importante!
        ch.setFormatter(new ColorFormatter());  // tu formatter con colores/emoji
        LOGGER.addHandler(ch);

        // (Opcional) Si quieres también afectar al root logger:
        Logger root = Logger.getLogger("");
        for (Handler h : root.getHandlers()) {
            h.setLevel(Level.ALL); // si decides mantenerlos
        }
    }

    public static Map<String, String> loadColors(String resourcePath) {
        Map<String, String> colorMap = new HashMap<>();


        try (InputStream inputStream = CssColorLoader.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                LOGGER.warning("No se encontró el recurso: " + resourcePath);
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
            LOGGER.severe(e.getMessage());
            e.printStackTrace();
        }

        return colorMap;
    }
}

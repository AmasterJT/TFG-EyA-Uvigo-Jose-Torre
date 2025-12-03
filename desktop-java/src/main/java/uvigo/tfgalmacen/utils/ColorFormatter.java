package uvigo.tfgalmacen.utils;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import static uvigo.tfgalmacen.utils.TerminalColors.*;

public class ColorFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
        String color;

        switch (record.getLevel().getName()) {
            case "FINE", "FINEST" -> color = VERDE;
            case "SEVERE" -> color = ROJO;
            case "WARNING" -> color = AMARILLO;
            case "INFO" -> color = CYAN;
            case "CONFIG" -> color = BLANCO;
            default -> color = RESET;
        }

        return String.format("%s%s [%s] %s%s%n",
                color,
                switch (record.getLevel().getName()) {
                    case "FINE", "FINEST" -> "✅";
                    case "SEVERE" -> "❌";
                    case "WARNING" -> "⚠️";
                    case "INFO" -> "ℹ️";
                    default -> "🧩";
                },
                record.getLevel().getName(),
                record.getMessage(),
                RESET
        );
    }
}

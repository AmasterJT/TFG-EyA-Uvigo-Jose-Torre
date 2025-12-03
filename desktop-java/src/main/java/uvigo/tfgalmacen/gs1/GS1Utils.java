package uvigo.tfgalmacen.gs1;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * GS1 utilities for generating GTIN-14 and SSCC codes, calculating check digits (mod10),
 * and formatting common Application Identifiers (AI) segments for GS1-128.
 * <p>
 * Designed for TFG JTTP (almacén intermedio) use-cases.
 */
public final class GS1Utils {

    private GS1Utils() {
    }

    // =====================
    // Check digit (mod10)
    // =====================

    /**
     * Calculates GS1 Mod10 check digit.
     *
     * @param numericWithoutCheck digits without the check digit (only 0-9)
     * @return check digit 0..9
     */
    public static int checkDigitMod10(String numericWithoutCheck) {
        requireNumeric(numericWithoutCheck);
        int sum = 0;
        boolean triple = true; // from right to left, weights 3,1,3,1...
        for (int i = numericWithoutCheck.length() - 1; i >= 0; i--) {
            int d = numericWithoutCheck.charAt(i) - '0';
            sum += d * (triple ? 3 : 1);
            triple = !triple;
        }
        int mod = sum % 10;
        return (mod == 0) ? 0 : 10 - mod;
    }

    // =====================
    // GTIN generation
    // =====================

    /**
     * Builds a GTIN-14 from components:
     * <pre>
     *  indicatorDigit (1) + companyPrefix + itemReference + checkDigit = 14 digits
     * </pre>
     * The concatenation of companyPrefix + itemReference must be between 6 and 12 digits so that
     * indicatorDigit + companyPrefix + itemReference = 13 digits (before check).
     */
    public static String buildGTIN14(String indicatorDigit,
                                     String companyPrefix,
                                     String itemReference) {
        requireNumeric(indicatorDigit);
        requireNumeric(companyPrefix);
        requireNumeric(itemReference);
        if (indicatorDigit.length() != 1) {
            throw new IllegalArgumentException("indicatorDigit must be exactly 1 digit");
        }

        // Build body (13 digits without check)
        String body = indicatorDigit + companyPrefix + itemReference;
        if (body.length() > 13) {
            throw new IllegalArgumentException(
                    "indicatorDigit + companyPrefix + itemReference must be <= 13 digits (is " + body.length() + ")"
            );
        }
        // Left-pad itemReference if necessary to reach 13
        if (body.length() < 13) {
            int pad = 13 - body.length();
            StringBuilder sb = new StringBuilder();
            sb.append(indicatorDigit).append(companyPrefix);
            for (int i = 0; i < pad; i++) {
                sb.append('0');
            }
            sb.append(itemReference);
            body = sb.toString();
        }

        int check = checkDigitMod10(body);
        return body + check;
    }

    /**
     * Convenience: generates GTIN-14 using numeric components.
     */
    public static String generateGTIN14(int indicatorDigit, String companyPrefix, long itemRef) {
        if (indicatorDigit < 0 || indicatorDigit > 9) {
            throw new IllegalArgumentException("indicatorDigit must be 0..9");
        }
        return buildGTIN14(String.valueOf(indicatorDigit), companyPrefix, String.valueOf(itemRef));
    }

    // =====================
    // SSCC generation
    // =====================

    /**
     * Builds SSCC (18 digits) = extension (1) + companyPrefix + serial (padded) + check
     *
     * @param extensionDigit 0..9
     * @param companyPrefix  GS1 company prefix (numeric)
     * @param serial         numeric serial (your own sequence)
     * @return SSCC 18-digit numeric string
     */
    public static String generateSSCC(int extensionDigit, String companyPrefix, long serial) {
        if (extensionDigit < 0 || extensionDigit > 9) {
            throw new IllegalArgumentException("extensionDigit must be 0..9");
        }
        requireNumeric(companyPrefix);
        if (companyPrefix.length() < 6 || companyPrefix.length() > 12) {
            // GS1 company prefixes vary in length; typical 7-10. We allow a sane range.
            throw new IllegalArgumentException("companyPrefix length must be between 6 and 12 digits");
        }
        String serialStr = String.valueOf(serial);
        requireNumeric(serialStr);

        // Build the 17-digit base (without check): ext + prefix + serial(left-padded)
        int baseLenWithoutCheck = 17;
        String baseNoCheck = extensionDigit + companyPrefix;
        int remaining = baseLenWithoutCheck - baseNoCheck.length();
        if (remaining <= 0) {
            throw new IllegalArgumentException("companyPrefix too long for SSCC base");
        }
        if (serialStr.length() > remaining) {
            throw new IllegalArgumentException("serial too long for SSCC with this companyPrefix");
        }

        String serialPadded = leftPad(serialStr, remaining, '0');
        String body = baseNoCheck + serialPadded;

        int check = checkDigitMod10(body);
        return body + check;
    }

    // =====================
    // AI format helpers
    // =====================

    public static String ai01(String gtin14) {
        requireNumeric(gtin14);
        if (gtin14.length() != 14) {
            throw new IllegalArgumentException("GTIN-14 must be 14 digits");
        }
        return "(01)" + gtin14;
    }

    /**
     * AI(37) - Count of trade items (up to 8 digits).
     */
    public static String ai37(int quantity) {
        if (quantity < 0 || quantity > 99999999) {
            throw new IllegalArgumentException("quantity must be 0..99999999");
        }
        return "(37)" + quantity;
    }

    /**
     * AI(10) - Batch or lot number. Variable-length up to 20 alphanumeric characters.
     */
    public static String ai10(String lot) {
        Objects.requireNonNull(lot, "lot");
        // AI(10) is variable length up to 20 alphanumerics
        if (lot.isEmpty() || lot.length() > 20) {
            throw new IllegalArgumentException("lot must be 1..20 chars");
        }
        return "(10)" + lot;
    }

    /**
     * AI(15) - Best before date (YYMMDD).
     */
    public static String ai15(LocalDate date) {
        Objects.requireNonNull(date, "date");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyMMdd");
        return "(15)" + date.format(fmt);
    }

    /**
     * AI(00) SSCC
     */
    public static String ai00(String sscc) {
        requireNumeric(sscc);
        if (sscc.length() != 18) {
            throw new IllegalArgumentException("SSCC must be 18 digits");
        }
        return "(00)" + sscc;
    }

    // =====================
    // Helpers
    // =====================

    private static void requireNumeric(String s) {
        if (s == null || !s.matches("\\d+")) {
            throw new IllegalArgumentException("numeric string expected");
        }
    }

    private static String leftPad(String s, int width, char ch) {
        if (s.length() >= width) {
            return s;
        }
        StringBuilder sb = new StringBuilder(width);
        for (int i = s.length(); i < width; i++) {
            sb.append(ch);
        }
        sb.append(s);
        return sb.toString();
    }


}

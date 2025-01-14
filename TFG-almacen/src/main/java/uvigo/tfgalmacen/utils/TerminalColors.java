package uvigo.tfgalmacen.utils;


public class TerminalColors {
    // Esta clase establece colores para escribir texto por consola

    // sintaxis del trexto (ejemplo):
    //      String texto = "Hola mundo";
    //      String texto_con_colores = Colores.ROJO + texto + Colores.RESET_COLOR;
    //      System.out.println(texto_con_colores);

    // Definir colores con nombres descriptivos
    public static String ROJO = "\u001B[31m";
    public static String VERDE = "\u001B[32m";
    public static String NARANJA = "\033[34m";  // Azul
    public static String AMARILLO = "\u001B[33m";
    public static String MAGENTA = "\u001B[35m";
    public static String CYAN = "\u001B[36m";
    public static String BLANCO = "\u001B[37m";
    public static String GRIS = "\u001B[90m";
    public static String ROJO_CLARO = "\u001B[91m";
    public static String VERDE_CLARO = "\u001B[92m";
    public static String AMARILLO_CLARO = "\u001B[93m";
    public static String AZUL_CLARO = "\u001B[94m";
    public static String MAGENTA_CLARO = "\u001B[95m";
    public static String CYAN_CLARO = "\u001B[96m";
    public static String BLANCO_BRILLANTE = "\u001B[97m";
    public static String FONDO_ROJO = "\u001B[41m";
    public static String FONDO_VERDE = "\u001B[42m";
    public static String FONDO_AMARILLO = "\u001B[43m";
    public static String FONDO_AZUL = "\u001B[44m";
    public static String FONDO_MAGENTA = "\u001B[45m";

    public static String RESET = "\u001B[0m";


    public static int rgbToAnsi(int r, int g, int b) {
        // Convertir el color RGB al espacio de color CIE Lab
        double[] lab = rgbToLab(r, g, b);

        // Convertir el espacio de color Lab al espacio de color LCH
        double[] lch = labToLCH(lab[0], lab[1], lab[2]);

        // Calcular el código ANSI basado en la luminancia (L) y croma (C)
        int ansiCode = (int) Math.round(lch[1] * 0.29 + lch[0] * 0.49);

        // Asegurar que el código ANSI esté en el rango válido (0-255)
        return Math.max(0, Math.min(255, ansiCode));
    }
    // Método para convertir el color RGB al espacio de color CIE Lab
    public static double[] rgbToLab(int r, int g, int b) {
        // Convertir los valores RGB al rango [0, 1]
        double[] xyz = new double[3];
        xyz[0] = linearizeRGB(r / 255.0);
        xyz[1] = linearizeRGB(g / 255.0);
        xyz[2] = linearizeRGB(b / 255.0);

        // Convertir de RGB a XYZ
        xyz[0] = xyz[0] * 0.4124564 + xyz[1] * 0.3575761 + xyz[2] * 0.1804375;
        xyz[1] = xyz[0] * 0.2126729 + xyz[1] * 0.7151522 + xyz[2] * 0.0721750;
        xyz[2] = xyz[0] * 0.0193339 + xyz[1] * 0.1191920 + xyz[2] * 0.9503041;

        // Convertir de XYZ a CIE Lab
        xyz[0] /= 95.047;
        xyz[1] /= 100.0;
        xyz[2] /= 108.883;

        for (int i = 0; i < 3; i++) {
            xyz[i] = (xyz[i] > 0.008856) ? Math.cbrt(xyz[i]) : (xyz[i] * 7.787 + 16.0 / 116.0);
        }

        double[] lab = new double[3];
        lab[0] = 116 * xyz[1] - 16;
        lab[1] = 500 * (xyz[0] - xyz[1]);
        lab[2] = 200 * (xyz[1] - xyz[2]);

        return lab;
    }

    // Método para convertir el espacio de color Lab al espacio de color LCH
    public static double[] labToLCH(double l, double a, double b) {
        double c = Math.sqrt(a * a + b * b);
        double h = Math.atan2(b, a);

        // Convertir el ángulo de radianes a grados
        h = Math.toDegrees(h);
        if (h < 0) {
            h += 360;
        }

        return new double[]{l, c, h};
    }

    // Método para linearizar un valor de color RGB
    public static double linearizeRGB(double value) {
        return (value <= 0.04045) ? (value / 12.92) : Math.pow((value + 0.055) / 1.055, 2.4);
    }
}

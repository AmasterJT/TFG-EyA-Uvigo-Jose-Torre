package uvigo.tfgalmacen.utils;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class PasswordUtils {

    // Parámetros recomendados para Argon2id (ajusta según tu máquina)
    private static final int ITERATIONS = 3;     // tiempo
    private static final int MEMORY_KB = 65536; // 64 MB
    private static final int PARALLELISM = 2;    // nº hilos

    private static final Argon2 argon2 =
            Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

    /**
     * Genera un hash Argon2id para la contraseña dada.
     */
    public static String hashPassword(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password no puede ser null");
        }

        // argon2.hash(iterations, memory, parallelism, char[] password)
        return argon2.hash(ITERATIONS, MEMORY_KB, PARALLELISM, password.toCharArray());
    }

    /**
     * Verifica si una contraseña en texto plano coincide con un hash Argon2id.
     */
    public static boolean verifyPassword(String password, String hash) {
        if (password == null || hash == null) {
            return false;
        }
        return argon2.verify(hash, password.toCharArray());
    }

}
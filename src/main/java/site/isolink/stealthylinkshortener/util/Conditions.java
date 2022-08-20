package site.isolink.stealthylinkshortener.util;

import java.util.function.Supplier;

/**
 * Utility class for runtime checks.
 */
public class Conditions {
    /**
     * Checks whether condition is satisfied and throws specified exception if not.
     * @param condition condition to check
     * @param exceptionSupplier {@link Supplier} that provides specified type exception
     * @param <E> exception type
     * @throws E if condition is not satisfied
     */
    public static <E extends Exception> void require(boolean condition, Supplier<E> exceptionSupplier) throws E {
        if (!condition) {
            throw exceptionSupplier.get();
        }
    }
}

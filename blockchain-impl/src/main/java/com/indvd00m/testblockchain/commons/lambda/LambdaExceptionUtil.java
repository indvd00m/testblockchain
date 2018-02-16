package com.indvd00m.testblockchain.commons.lambda;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @see https://stackoverflow.com/a/27644392/6784078
 * 
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public final class LambdaExceptionUtil {

	@FunctionalInterface
	public interface Consumer_WithExceptions<T, E extends Exception> {
		void accept(T t) throws E;
	}

	@FunctionalInterface
	public interface BiConsumer_WithExceptions<T, U, E extends Exception> {
		void accept(T t, U u) throws E;
	}

	@FunctionalInterface
	public interface Function_WithExceptions<T, R, E extends Exception> {
		R apply(T t) throws E;
	}

	@FunctionalInterface
	public interface Supplier_WithExceptions<T, E extends Exception> {
		T get() throws E;
	}

	@FunctionalInterface
	public interface Runnable_WithExceptions<E extends Exception> {
		void run() throws E;
	}

	/**
	 * .forEach(rethrowConsumer(name -> System.out.println(Class.forName(name))));
	 * or .forEach(rethrowConsumer(ClassNameUtil::println));
	 */
	public static <T, E extends Exception> Consumer<T> rethrowConsumer(Consumer_WithExceptions<T, E> consumer)
			throws E {
		return t -> {
			try {
				consumer.accept(t);
			} catch (Exception exception) {
				throwActualException(exception);
			}
		};
	}

	public static <T, U, E extends Exception> BiConsumer<T, U> rethrowBiConsumer(
			BiConsumer_WithExceptions<T, U, E> biConsumer) throws E {
		return (t, u) -> {
			try {
				biConsumer.accept(t, u);
			} catch (Exception exception) {
				throwActualException(exception);
			}
		};
	}

	/**
	 * .map(rethrowFunction(name -> Class.forName(name))) or
	 * .map(rethrowFunction(Class::forName))
	 */
	public static <T, R, E extends Exception> Function<T, R> rethrowFunction(Function_WithExceptions<T, R, E> function)
			throws E {
		return t -> {
			try {
				return function.apply(t);
			} catch (Exception exception) {
				throwActualException(exception);
				return null;
			}
		};
	}

	/**
	 * rethrowSupplier(() -> new StringJoiner(new String(new byte[]{77, 97, 114,
	 * 107}, "UTF-8"))),
	 */
	public static <T, E extends Exception> Supplier<T> rethrowSupplier(Supplier_WithExceptions<T, E> function) {
		return () -> {
			try {
				return function.get();
			} catch (Exception exception) {
				throwActualException(exception);
				return null;
			}
		};
	}

	/** uncheck(() -> Class.forName("xxx")); */
	public static void uncheck(@SuppressWarnings("rawtypes") Runnable_WithExceptions t) {
		try {
			t.run();
		} catch (Exception exception) {
			throwAsUnchecked(exception);
		}
	}

	/** uncheck(() -> Class.forName("xxx")); */
	public static <R, E extends Exception> R uncheck(Supplier_WithExceptions<R, E> supplier) {
		try {
			return supplier.get();
		} catch (Exception exception) {
			throwAsUnchecked(exception);
			return null;
		}
	}

	/** uncheck(Class::forName, "xxx"); */
	public static <T, R, E extends Exception> R uncheck(Function_WithExceptions<T, R, E> function, T t) {
		try {
			return function.apply(t);
		} catch (Exception exception) {
			throwAsUnchecked(exception);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private static <E extends Exception> void throwActualException(Exception exception) throws E {
		throw (E) exception;
	}

	@SuppressWarnings("unchecked")
	private static <E extends Throwable> void throwAsUnchecked(Exception exception) throws E {
		throw (E) exception;
	}

}

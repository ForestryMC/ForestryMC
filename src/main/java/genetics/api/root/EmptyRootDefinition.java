package genetics.api.root;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import genetics.api.individual.IIndividual;

/**
 * A empty instance of an {@link IRootDefinition}.
 */
public final class EmptyRootDefinition<T extends IIndividualRoot<?>> implements IRootDefinition<T> {
	private static final EmptyRootDefinition<IIndividualRoot<?>> INSTANCE = new EmptyRootDefinition<>();
	// ? extends IIndividualRoot<IIndividual>
	public static <R extends IIndividualRoot<?>> IRootDefinition<R> empty() {
		@SuppressWarnings("unchecked")
		IRootDefinition<R> t = (IRootDefinition<R>) INSTANCE;
		return t;
	}

	private EmptyRootDefinition() {
	}

	@Override
	public Optional<T> maybe() {
		return Optional.empty();
	}

	@Override
	public T get() {
		throw new NullPointerException();
	}

	@Override
	public <U extends IIndividualRoot> U cast() {
		return (U)get();
	}

	@Override
	public boolean isPresent() {
		return false;
	}

	@Override
	public T orElse(T other) {
		return other;
	}

	@Override
	public boolean test(Predicate predicate) {
		return false;
	}

	@Override
	public Optional filter(Predicate predicate) {
		return Optional.empty();
	}

	@Override
	public void ifPresent(Consumer consumer) {
		//The optional is empty, so we have nothing to call.
	}

	@Override
	public Optional map(Function mapper) {
		return Optional.empty();
	}
}

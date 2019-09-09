package genetics.api.root;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A empty instance of an {@link IRootDefinition}.
 */
public final class EmptyRootDefinition implements IRootDefinition {
	private static final EmptyRootDefinition INSTANCE = new EmptyRootDefinition();

	public static <R extends IIndividualRoot> IRootDefinition<R> empty() {
		@SuppressWarnings("unchecked")
		IRootDefinition<R> t = (IRootDefinition<R>) INSTANCE;
		return t;
	}

	private EmptyRootDefinition() {
	}

	@Override
	public Optional<IIndividualRoot> maybe() {
		return Optional.empty();
	}

	@Override
	public IIndividualRoot get() {
		throw new NullPointerException();
	}

	@Override
	public boolean isRootPresent() {
		return false;
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

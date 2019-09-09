package genetics.individual;

import javax.annotation.Nullable;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import genetics.api.root.IIndividualRoot;
import genetics.api.root.IRootDefinition;

public class RootDefinition<R extends IIndividualRoot> implements IRootDefinition<R> {
	@Nullable
	private R root = null;

	public void setRoot(@Nullable R definition) {
		this.root = definition;
	}

	@Override
	public Optional<R> maybe() {
		return Optional.ofNullable(root);
	}

	@Override
	public R get() {
		if (root == null) {
			throw new NoSuchElementException("No value present");
		}
		return root;
	}

	@Override
	public boolean isRootPresent() {
		return root != null;
	}

	@Override
	public void ifPresent(Consumer<R> consumer) {
		if (root != null) {
			consumer.accept(root);
		}
	}

	@Override
	public <U> Optional<U> map(Function<? super R, ? extends U> mapper) {
		Objects.requireNonNull(mapper);
		if (!isRootPresent()) {
			return Optional.empty();
		} else {
			return Optional.ofNullable(mapper.apply(root));
		}
	}
}

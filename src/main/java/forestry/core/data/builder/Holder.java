package forestry.core.data.builder;

import javax.annotation.Nullable;

/**
 * Helper class to contain a single value.
 *
 * @param <T> The type of the contained value
 */
class Holder<T> {
	@Nullable
	private T object;

	public T get() {
		if (object == null) {
			throw new IllegalStateException("No value was returned during the creation of the recipe..");
		}
		return object;
	}

	public void set(T object) {
		this.object = object;
	}
}

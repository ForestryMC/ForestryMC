package forestry.core.data.builder;

class Holder<T> {
	private T object;

	public T get() {
		return object;
	}

	public void set(T object) {
		this.object = object;
	}
}
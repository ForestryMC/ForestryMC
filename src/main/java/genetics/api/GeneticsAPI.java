package genetics.api;

public class GeneticsAPI {
	/**
	 * An object that contains getter-methods almost every registry of the genetics mod.
	 * <p>
	 * If the mod is not present this field only contains the {@link DummyApiInstance} and will throw an
	 * {@link IllegalStateException} every time you try to get a registry.
	 * <p>
	 * Please call {@link IGeneticApiInstance#isModPresent()} before you use one of the other methods.
	 * The methods will also throw an {@link IllegalStateException} if you call them to early in the
	 * registration cycle.
	 */
	public static IGeneticApiInstance apiInstance = new DummyApiInstance();

	private GeneticsAPI() {
	}
}

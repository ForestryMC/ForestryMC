package genetics.api.classification;

public interface IClassificationHandler {
	/**
	 * Called when a classification is registered with {@link IClassificationRegistry}.
	 *
	 * @param classification Classification which was registered.
	 */
	void onRegisterClassification(IClassification classification);
}

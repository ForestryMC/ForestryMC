package genetics.api.classification;

import java.util.Collection;
import java.util.Map;

import genetics.api.classification.IClassification.EnumClassLevel;

/**
 * Main interface for the registration of {@link IClassification}s.
 */
public interface IClassificationRegistry {
	/**
	 * @return HashMap of all currently registered classifications.
	 */
	Map<String, IClassification> getRegisteredClassifications();

	/**
	 * Registers a classification.
	 *
	 * @param classification IClassification to register.
	 */
	void registerClassification(IClassification classification);

	/**
	 * Creates and returns a classification.
	 *
	 * @param level      EnumClassLevel of the classification to create.
	 * @param uid        String based unique identifier. Implementation will throw an exception if the key is already taken.
	 * @param scientific Binomial for the given classification.
	 * @return Created {@link IClassification} for easier chaining.
	 */
	IClassification createAndRegisterClassification(EnumClassLevel level, String uid, String scientific);

	IClassification createAndRegisterClassification(EnumClassLevel level, String uid, String scientific, IClassification... members);

	/**
	 * Gets a classification.
	 *
	 * @param uid String based unique identifier of the classification to retrieve.
	 * @return {@link IClassification} if found, null otherwise.
	 */
	IClassification getClassification(String uid);

	/**
	 * Registers a new IClassificationHandler
	 *
	 * @param handler IClassificationHandler to register.
	 */
	void registerHandler(IClassificationHandler handler);

	/**
	 * @return all handlers that were registered.
	 */
	Collection<IClassificationHandler> getHandlers();
}

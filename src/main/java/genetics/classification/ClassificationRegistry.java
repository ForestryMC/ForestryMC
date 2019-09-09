package genetics.classification;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import genetics.api.classification.IClassification;
import genetics.api.classification.IClassification.EnumClassLevel;
import genetics.api.classification.IClassificationHandler;
import genetics.api.classification.IClassificationRegistry;

public class ClassificationRegistry implements IClassificationRegistry {
	private final LinkedHashMap<String, IClassification> classificationMap = new LinkedHashMap<>(128);
	/*
	 * Internal Set of all alleleHandlers, which trigger when an branch is registered
	 */
	private final Set<IClassificationHandler> classificationHandlers = new HashSet<>();

	@Override
	public void registerClassification(IClassification branch) {
		if (classificationMap.containsKey(branch.getUID())) {
			throw new IllegalArgumentException(String.format("Could not add new classification '%s', because the key is already taken by %s.", branch.getUID(),
				classificationMap.get(branch.getUID())));
		}

		classificationMap.put(branch.getUID(), branch);
		for (IClassificationHandler handler : classificationHandlers) {
			handler.onRegisterClassification(branch);
		}
	}

	@Override
	public Map<String, IClassification> getRegisteredClassifications() {
		return Collections.unmodifiableMap(classificationMap);
	}

	@Override
	public IClassification createAndRegisterClassification(EnumClassLevel level, String uid, String scientific) {
		return new Classification(level, uid, scientific);
	}

	@Override
	public IClassification createAndRegisterClassification(EnumClassLevel level, String uid, String scientific, IClassification... members) {
		IClassification classification = new Classification(level, uid, scientific);
		for (IClassification member : members) {
			classification.addMemberGroup(member);
		}
		return classification;
	}

	@Override
	public IClassification getClassification(String uid) {
		return classificationMap.get(uid);
	}

	@Override
	public void registerHandler(IClassificationHandler handler) {
		classificationHandlers.add(handler);
	}

	@Override
	public Collection<IClassificationHandler> getHandlers() {
		return classificationHandlers;
	}
}

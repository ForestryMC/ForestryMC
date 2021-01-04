package genetics.root;

import genetics.api.root.components.ComponentKey;
import genetics.api.root.components.ComponentKeys;
import genetics.api.root.components.IRootComponentFactory;
import genetics.api.root.components.IRootComponentRegistry;
import genetics.organism.OrganismTypes;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public enum RootComponentRegistry implements IRootComponentRegistry {
	INSTANCE;

	private final Map<ComponentKey, IRootComponentFactory> factoryByKey = new HashMap<>();

	RootComponentRegistry() {
		registerFactory(ComponentKeys.TEMPLATES, TemplateContainer::new);
		registerFactory(ComponentKeys.TYPES, OrganismTypes::new);
		registerFactory(ComponentKeys.TRANSLATORS, IndividualTranslator::new);
		registerFactory(ComponentKeys.MUTATIONS, MutationContainer::new);
	}

	@Override
	public void registerFactory(ComponentKey key, IRootComponentFactory factory) {
		factoryByKey.put(key, factory);
	}

	@Nullable
	@Override
	public IRootComponentFactory getFactory(ComponentKey key) {
		return factoryByKey.get(key);
	}
}

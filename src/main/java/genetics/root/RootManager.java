package genetics.root;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import genetics.api.GeneticsAPI;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IIndividual;
import genetics.api.root.IGeneticListener;
import genetics.api.root.IGeneticListenerRegistry;
import genetics.api.root.IIndividualRootBuilder;
import genetics.api.root.IRootManager;

public class RootManager implements IRootManager, IGeneticListenerRegistry {
	private final HashMap<String, IndividualRootBuilder> rootBuilders = new HashMap<>();
	private final Multimap<String, IGeneticListener> listeners = HashMultimap.create();

	@Override
	public <I extends IIndividual> IIndividualRootBuilder<I> createRoot(String uid) {
		IndividualRootBuilder<I> builder = new IndividualRootBuilder<>(uid);
		builder.addChromosome(GeneticsAPI.apiInstance.getChromosomeList(uid).typesArray());
		rootBuilders.put(uid, builder);
		return builder;
	}

	@Override
	public <I extends IIndividual, T extends Enum<T> & IChromosomeType> IIndividualRootBuilder<I> createRoot(String uid, Class<? extends T> enumClass) {
		T[] types = enumClass.getEnumConstants();
		if (types.length <= 0) {
			throw new IllegalArgumentException("The given enum class must contain at least one enum constant.");
		}
		IndividualRootBuilder<I> builder = new IndividualRootBuilder<>(uid);
		for (int i = 1; i < types.length; i++) {
			IChromosomeType type = types[i];
			builder.addChromosome(type);
		}
		rootBuilders.put(uid, builder);
		return builder;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <I extends IIndividual> Optional<IIndividualRootBuilder<I>> getRoot(String uid) {
		return Optional.ofNullable((IndividualRootBuilder<I>) rootBuilders.get(uid));
	}

	@Override
	public void add(String uid, IGeneticListener listener) {
		this.listeners.put(uid, listener);
	}

	@Override
	public void add(String uid, IGeneticListener... listeners) {
		add(uid, Lists.newArrayList(listeners));
	}

	@Override
	public void add(String uid, Collection<IGeneticListener> listeners) {
		this.listeners.putAll(uid, listeners);
	}

	public Collection<IGeneticListener> getListeners(String uid) {
		return this.listeners.get(uid);
	}

	public Multimap<String, IGeneticListener> getListeners() {
		return listeners;
	}

	public Map<String, IndividualRootBuilder> getRootBuilders() {
		return rootBuilders;
	}
}

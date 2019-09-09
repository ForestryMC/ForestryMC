package genetics.organism;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import net.minecraft.item.ItemStack;

import genetics.api.GeneticHelper;
import genetics.api.individual.IIndividual;
import genetics.api.organism.IOrganism;
import genetics.api.organism.IOrganismHandler;
import genetics.api.organism.IOrganismType;
import genetics.api.organism.IOrganismTypes;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.components.ComponentKey;
import genetics.api.root.components.ComponentKeys;

import genetics.GeneticFactory;
import genetics.Genetics;
import genetics.Log;

public class OrganismTypes<I extends IIndividual> implements IOrganismTypes<I> {
	private final Map<IOrganismType, IOrganismHandler<I>> types = new LinkedHashMap<>();
	private final IIndividualRoot<I> root;
	@Nullable
	private IOrganismType defaultType;

	public OrganismTypes(IIndividualRoot<I> root) {
		this.root = root;
	}

	@Override
	public IIndividualRoot<I> getRoot() {
		return root;
	}

	@Override
	public IOrganismTypes<I> registerType(IOrganismType type, IOrganismHandler<I> handler, boolean defaultType) {
		types.put(type, handler);
		if (defaultType) {
			this.defaultType = type;
		}
		return this;
	}

	@Override
	public IOrganismTypes<I> registerType(IOrganismType type, Supplier<ItemStack> stack, boolean defaultType) {
		return registerType(type, GeneticFactory.INSTANCE.createOrganismHandler(root.getDefinition(), stack), defaultType);
	}

	@Override
	public ItemStack createStack(I individual, IOrganismType type) {
		IOrganismHandler<I> handler = types.get(type);
		if (handler == null) {
			return ItemStack.EMPTY;
		}
		return handler.createStack(individual);
	}

	@Override
	public Optional<I> createIndividual(ItemStack itemStack) {
		Optional<IOrganismType> optional = getType(itemStack);
		if (!optional.isPresent()) {
			return Optional.empty();
		}
		IOrganismHandler<I> handler = types.get(optional.get());
		if (handler == null) {
			return Optional.empty();
		}
		return handler.createIndividual(itemStack);
	}

	@Override
	public boolean setIndividual(ItemStack itemStack, I individual) {
		Optional<IOrganismType> optional = getType(itemStack);
		if (!optional.isPresent()) {
			return false;
		}
		IOrganismHandler<I> handler = types.get(optional.get());
		if (handler == null) {
			return false;
		}
		return handler.setIndividual(itemStack, individual);
	}

	@Override
	public Optional<IOrganismType> getType(ItemStack itemStack) {
		IOrganism organism = itemStack.getCapability(Genetics.ORGANISM).orElse(GeneticHelper.EMPTY);
		return organism.isEmpty() ? Optional.empty() : Optional.of(organism.getType());
	}

	@Override
	public IOrganismType getDefaultType() {
		if (defaultType == null) {
			Iterator<IOrganismType> organismTypes = types.keySet().iterator();
			if (!organismTypes.hasNext()) {
				String message = String.format("No types were registered for the individual root '%s'.", root.getUID());
				throw new IllegalStateException(message);
			}
			defaultType = organismTypes.next();
			Log.debug("No default type was registered for individual root '{}' used first registered type.", root.getUID());
		}
		return defaultType;
	}

	@Override
	public Optional<IOrganismHandler<I>> getHandler(IOrganismType type) {
		return Optional.ofNullable(types.get(type));
	}

	@Override
	public Optional<IOrganismHandler<I>> getHandler(ItemStack itemStack) {
		Optional<IOrganismType> type = getType(itemStack);
		return type.flatMap(this::getHandler);
	}

	@Override
	public Collection<IOrganismType> getTypes() {
		return types.keySet();
	}

	@Override
	public Collection<IOrganismHandler<I>> getHandlers() {
		return types.values();
	}

	@Override
	public ComponentKey<IOrganismTypes> getKey() {
		return ComponentKeys.TYPES;
	}
}

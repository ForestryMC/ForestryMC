package genetics.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;

import genetics.api.alleles.Allele;
import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleValue;
import genetics.api.individual.IChromosomeAllele;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IChromosomeValue;
import genetics.api.individual.IIndividual;
import genetics.api.organism.EmptyOrganismType;
import genetics.api.organism.IOrganism;
import genetics.api.organism.IOrganismHandler;
import genetics.api.organism.IOrganismType;
import genetics.api.root.EmptyRootDefinition;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.IRootDefinition;

/**
 * A helper class that contains some help methods.
 */
public class GeneticHelper {

	@CapabilityInject(IOrganism.class)
	public static Capability<IOrganism> ORGANISM;
	public static final IOrganism<?> EMPTY = EmptyOrganism.INSTANCE;

	private GeneticHelper() {
	}

	public static <I extends IIndividual> IOrganism<I> createOrganism(ItemStack itemStack, IOrganismType type, IRootDefinition<? extends IIndividualRoot<I>> root) {
		IGeneticFactory geneticFactory = GeneticsAPI.apiInstance.getGeneticFactory();
		return geneticFactory.createOrganism(itemStack, type, root);
	}

	@SuppressWarnings("unchecked")
	public static <I extends IIndividual> IOrganism<I> getOrganism(ItemStack itemStack) {
		return itemStack.getCapability(ORGANISM).orElse(EMPTY);
	}

	public static <I extends IIndividual> boolean setIndividual(ItemStack itemStack, I individual) {
		IOrganism<I> organism = getOrganism(itemStack);
		return organism.setIndividual(individual);
	}

	@SuppressWarnings("unchecked")
	public static <I extends IIndividual> Optional<I> getIndividual(ItemStack itemStack) {
		return itemStack.getCapability(ORGANISM).orElse(EMPTY).getIndividual();
	}

	public static IOrganismHandler getOrganismHandler(IIndividualRoot<IIndividual> root, IOrganismType type) {
		Optional<IOrganismHandler<IIndividual>> optionalHandler = root.getTypes().getHandler(type);
		if (!optionalHandler.isPresent()) {
			throw new IllegalArgumentException(String.format("No organism handler was registered for the organism type '%s'", type.getName()));
		}
		return optionalHandler.get();
	}

	private enum EmptyOrganism implements IOrganism<IIndividual> {
		INSTANCE;


		@Override
		public Optional<IIndividual> getIndividual() {
			return Optional.empty();
		}

		@Override
		public boolean setIndividual(IIndividual individual) {
			return false;
		}

		@Override
		public IRootDefinition<? extends IIndividualRoot<IIndividual>> getDefinition() {
			return EmptyRootDefinition.empty();
		}

		@Override
		public IOrganismType getType() {
			return EmptyOrganismType.INSTANCE;
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public IAllele getAllele(IChromosomeType type, boolean active) {
			return Allele.EMPTY;
		}

		@Override
		@SuppressWarnings("unchecked")
		public <A extends IAllele> A getAllele(IChromosomeAllele<A> type, boolean active) {
			return (A) Allele.EMPTY;
		}

		@Override
		@SuppressWarnings("unchecked")
		public <V> IAlleleValue<V> getAllele(IChromosomeValue<V> type, boolean active) {
			return (IAlleleValue<V>) Allele.EMPTY;
		}


		@Override
		public Optional<IAllele> getAlleleDirectly(IChromosomeType type, boolean active) {
			return Optional.empty();
		}

		@Nonnull
		@Override
		public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
			return LazyOptional.empty();
		}
	}
}

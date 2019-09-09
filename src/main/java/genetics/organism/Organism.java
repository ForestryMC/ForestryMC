package genetics.organism;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleValue;
import genetics.api.individual.IChromosomeAllele;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IChromosomeValue;
import genetics.api.individual.IIndividual;
import genetics.api.organism.IOrganism;
import genetics.api.organism.IOrganismType;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.IRootDefinition;

import genetics.Genetics;
import genetics.individual.GeneticSaveHandler;

public class Organism<I extends IIndividual> implements IOrganism<I> {
	private final LazyOptional<IOrganism> holder = LazyOptional.of(() -> this);
	private final ItemStack container;
	private final IRootDefinition<? extends IIndividualRoot<I>> definition;
	private final Supplier<IOrganismType> typeSupplier;

	public Organism(ItemStack container, IRootDefinition<? extends IIndividualRoot<I>> geneticDefinitionSupplier, Supplier<IOrganismType> typeSupplier) {
		this.container = container;
		this.definition = geneticDefinitionSupplier;
		this.typeSupplier = typeSupplier;
	}

	@Override
	public Optional<I> getIndividual() {
		return getDefinition().get().getTypes().createIndividual(container);
	}

	@Override
	public boolean setIndividual(I individual) {
		return getDefinition().get().getTypes().setIndividual(container, individual);
	}

	@Override
	public IRootDefinition<? extends IIndividualRoot<I>> getDefinition() {
		return definition;
	}

	@Override
	public IOrganismType getType() {
		return typeSupplier.get();
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public IAllele getAllele(IChromosomeType chromosomeType, boolean active) {
		IAllele allele = GeneticSaveHandler.INSTANCE.getAlleleDirectly(container, getType(), chromosomeType, active);
		if (allele == null) {
			allele = GeneticSaveHandler.INSTANCE.getAllele(container, getType(), chromosomeType, active);
		}
		return allele;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <A extends IAllele> A getAllele(IChromosomeAllele<A> type, boolean active) {
		IAllele allele = GeneticSaveHandler.INSTANCE.getAlleleDirectly(container, getType(), type, active);
		if (allele == null) {
			allele = GeneticSaveHandler.INSTANCE.getAllele(container, getType(), type, active);
		}
		return (A) allele;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> IAlleleValue<V> getAllele(IChromosomeValue<V> type, boolean active) {
		IAllele allele = GeneticSaveHandler.INSTANCE.getAlleleDirectly(container, getType(), type, active);
		if (allele == null) {
			allele = GeneticSaveHandler.INSTANCE.getAllele(container, getType(), type, active);
		}
		return (IAlleleValue<V>) allele;
	}

	@Override
	public Optional<IAllele> getAlleleDirectly(IChromosomeType type, boolean active) {
		return Optional.ofNullable(GeneticSaveHandler.INSTANCE.getAlleleDirectly(container, getType(), type, active));
	}

	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction facing) {
		return Genetics.ORGANISM.orEmpty(cap, holder);
	}
}

package forestry.core.genetics;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IIndividualHandler;
import forestry.api.genetics.ISpeciesRoot;
import forestry.api.genetics.ISpeciesType;
import forestry.core.CoreCapabilities;

public class IndividualHandler<I extends IIndividual> implements IIndividualHandler<I>, ICapabilityProvider {
	private final ItemStack container;
	private final Supplier<ISpeciesRoot> rootSupplier;
	private final Supplier<ISpeciesType> typeSupplier;

	public IndividualHandler(ItemStack container, Supplier<ISpeciesRoot> rootSupplier, Supplier<ISpeciesType> typeSupplier) {
		this.container = container;
		this.rootSupplier = rootSupplier;
		this.typeSupplier = typeSupplier;
	}

	@Override
	public I getIndividual() {
		return (I)getRoot().getMember(container);
	}

	@Override
	public ISpeciesRoot getRoot() {
		return rootSupplier.get();
	}

	@Override
	public ISpeciesType getType() {
		return typeSupplier.get();
	}

	@Override
	public IAllele getAlleleDirectly(IChromosomeType type, boolean active) {
		IAllele allele = Genome.getAlleleDirectly(type, active, container);
		if(allele == null){
			allele = Genome.getAllele(container, type, active);
		}
		return allele;
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CoreCapabilities.INDIVIDUAL_HANDLER;
	}

	@Nullable
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		return capability == CoreCapabilities.INDIVIDUAL_HANDLER ? CoreCapabilities.INDIVIDUAL_HANDLER.cast(this) : null;
	}
}

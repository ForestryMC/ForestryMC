package forestry.core.genetics;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;

import net.minecraft.item.ItemStack;

import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IAlyzerPlugin;
import forestry.api.genetics.IDatabasePlugin;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.ISpeciesDisplayHelper;
import forestry.api.genetics.ISpeciesRoot;
import forestry.api.genetics.ISpeciesType;

public class SpeciesDisplayHelper implements ISpeciesDisplayHelper {
	private final Table<ISpeciesType, String, ItemStack> iconStacks = HashBasedTable.create();
	@Nullable
	private final IAlyzerPlugin alyzerPlugin;
	@Nullable
	private final IDatabasePlugin databasePlugin;
	private final ISpeciesRoot root;

	public SpeciesDisplayHelper(ISpeciesRoot root, @Nullable Function<ISpeciesDisplayHelper, IAlyzerPlugin> alyzerPlugin, @Nullable Function<ISpeciesDisplayHelper, IDatabasePlugin> databasePlugin) {
		this.root = root;
		for (IIndividual individual : root.getIndividualTemplates()) {
			for (ISpeciesType type : root.getTypes()) {
				ItemStack itemStack = root.getMemberStack(individual, type);
				iconStacks.put(type, individual.getGenome().getPrimary().getUID(), itemStack);
			}
		}
		this.alyzerPlugin = alyzerPlugin == null ? null : alyzerPlugin.apply(this);
		this.databasePlugin = databasePlugin == null ? null : databasePlugin.apply(this);
	}

	@Override
	public Optional<IDatabasePlugin> getDatabasePlugin() {
		return Optional.ofNullable(databasePlugin);
	}

	@Override
	public Optional<IAlyzerPlugin> getAlyzerPlugin() {
		return Optional.ofNullable(alyzerPlugin);
	}

	@Override
	public ItemStack getDisplayStack(IAlleleSpecies species, ISpeciesType type) {
		return iconStacks.get(species.getUID(), type);
	}

	@Override
	public ItemStack getDisplayStack(IAlleleSpecies species) {
		return getDisplayStack(species, root.getIconType());
	}
}

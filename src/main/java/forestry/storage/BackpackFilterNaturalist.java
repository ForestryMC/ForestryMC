package forestry.storage;

import javax.annotation.Nonnull;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.ISpeciesRoot;
import forestry.api.storage.IBackpackFilter;
import net.minecraft.item.ItemStack;

public class BackpackFilterNaturalist implements IBackpackFilter {
	@Nonnull
	private final String speciesRootUid;

	public BackpackFilterNaturalist(@Nonnull String speciesRootUid) {
		this.speciesRootUid = speciesRootUid;
	}

	@Override
	public boolean test(ItemStack itemStack) {
		ISpeciesRoot speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(speciesRootUid);
		return speciesRoot.isMember(itemStack);
	}
}

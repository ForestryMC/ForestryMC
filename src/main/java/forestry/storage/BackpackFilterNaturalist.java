package forestry.storage;

import java.util.function.Predicate;

import net.minecraft.item.ItemStack;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.ISpeciesRoot;

public class BackpackFilterNaturalist implements Predicate<ItemStack> {
	private final String speciesRootUid;

	public BackpackFilterNaturalist(String speciesRootUid) {
		this.speciesRootUid = speciesRootUid;
	}

	@Override
	public boolean test(ItemStack itemStack) {
		ISpeciesRoot speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(speciesRootUid);
		return speciesRoot != null && speciesRoot.isMember(itemStack);
	}
}

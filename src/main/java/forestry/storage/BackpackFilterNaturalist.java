package forestry.storage;

import java.util.function.Predicate;

import net.minecraft.item.ItemStack;

import genetics.api.GeneticsAPI;
import genetics.api.individual.IIndividual;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.IRootDefinition;

public class BackpackFilterNaturalist implements Predicate<ItemStack> {
	private final String speciesRootUid;

	public BackpackFilterNaturalist(String speciesRootUid) {
		this.speciesRootUid = speciesRootUid;
	}

	@Override
	public boolean test(ItemStack itemStack) {
		IRootDefinition<IIndividualRoot<IIndividual>> definition = GeneticsAPI.apiInstance.getRoot(speciesRootUid);
		return definition.isRootPresent() && definition.get().isMember(itemStack);
	}
}

package forestry.storage;

import genetics.api.GeneticsAPI;
import genetics.api.individual.IIndividual;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.IRootDefinition;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public class BackpackFilterNaturalist implements Predicate<ItemStack> {
    private final String speciesRootUid;

    public BackpackFilterNaturalist(String speciesRootUid) {
        this.speciesRootUid = speciesRootUid;
    }

    @Override
    public boolean test(ItemStack itemStack) {
        IRootDefinition<IIndividualRoot<IIndividual>> definition = GeneticsAPI.apiInstance.getRoot(speciesRootUid);
        return definition.test(root -> root.isMember(itemStack));
    }

}

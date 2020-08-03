package forestry.farming.logic.farmables;

import forestry.api.farming.IFarmableInfo;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FarmableInfo implements IFarmableInfo {
    private final List<ItemStack> germlings = new ArrayList<>();
    private final List<ItemStack> products = new ArrayList<>();
    private final String identifier;

    public FarmableInfo(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public void addSeedlings(Collection<ItemStack> seedlings) {
        this.germlings.addAll(seedlings);
    }

    @Override
    public Collection<ItemStack> getSeedlings() {
        return germlings;
    }

    @Override
    public void addProducts(Collection<ItemStack> products) {
        this.products.addAll(products);
    }

    @Override
    public Collection<ItemStack> getProducts() {
        return products;
    }
}

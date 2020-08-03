package forestry.farming.multiblock;

import forestry.api.farming.IFarmInventory;
import net.minecraft.item.ItemStack;

import java.util.Stack;

public interface IFarmInventoryInternal extends IFarmInventory {

    int getFertilizerValue();

    boolean useFertilizer();

    void stowProducts(Iterable<ItemStack> harvested, Stack<ItemStack> pendingProduce);

    boolean tryAddPendingProduce(Stack<ItemStack> pendingProduce);
}

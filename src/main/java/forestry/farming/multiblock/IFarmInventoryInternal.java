package forestry.farming.multiblock;

import java.util.Stack;

import net.minecraft.item.ItemStack;

import forestry.api.farming.IFarmInventory;

public interface IFarmInventoryInternal extends IFarmInventory {

    int getFertilizerValue();

    boolean useFertilizer();

    void stowProducts(Iterable<ItemStack> harvested, Stack<ItemStack> pendingProduce);

    boolean tryAddPendingProduce(Stack<ItemStack> pendingProduce);
}

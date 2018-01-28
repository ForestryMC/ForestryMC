package forestry.api.multiblock;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public interface IMultiblockBlueprint {
	IBlockState[][][] getBlockStates();

	NonNullList<ItemStack> getResources();

	int getXSize();

	int getYSize();

	int getZSize();
}

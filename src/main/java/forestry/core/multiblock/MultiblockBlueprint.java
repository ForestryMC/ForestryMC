package forestry.core.multiblock;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import forestry.api.multiblock.IMultiblockBlueprint;

public class MultiblockBlueprint implements IMultiblockBlueprint {
	private final IBlockState[][][] states;
	private final NonNullList<ItemStack> resources;
	private final int width;
	private final int height;
	private final int length;

	public MultiblockBlueprint(IBlockState[][][] states, int width, int height, int length, ItemStack... resources) {
		this.states = states;
		this.resources = NonNullList.from(ItemStack.EMPTY, resources);
		this.width = width;
		this.height = height;
		this.length = length;
	}

	@Override
	public IBlockState[][][] getBlockStates() {
		return states;
	}

	@Override
	public NonNullList<ItemStack> getResources() {
		return resources;
	}

	@Override
	public int getXSize() {
		return width;
	}

	@Override
	public int getYSize() {
		return height;
	}

	@Override
	public int getZSize() {
		return length;
	}
}

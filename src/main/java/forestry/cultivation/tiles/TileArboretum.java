package forestry.cultivation.tiles;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class TileArboretum extends TilePlanter {
	public TileArboretum() {
		super("farmArboreal");
	}

	@Override
	public ItemStack[] createGermlingStacks() {
		return new ItemStack[]{
			new ItemStack(Blocks.SAPLING),
			new ItemStack(Blocks.SAPLING, 1, 2),
			new ItemStack(Blocks.SAPLING, 1, 2),
			new ItemStack(Blocks.SAPLING)
		};
	}

	@Override
	public ItemStack[] createResourceStacks() {
		return new ItemStack[]{
			new ItemStack(Blocks.DIRT),
			new ItemStack(Blocks.DIRT),
			new ItemStack(Blocks.DIRT),
			new ItemStack(Blocks.DIRT)
		};
	}

	@Override
	public ItemStack[] createProductionStacks() {
		return new ItemStack[]{
			new ItemStack(Blocks.LOG),
			new ItemStack(Items.APPLE),
			new ItemStack(Items.APPLE),
			new ItemStack(Blocks.LOG)
		};
	}
}

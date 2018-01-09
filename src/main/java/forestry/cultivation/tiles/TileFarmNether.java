package forestry.cultivation.tiles;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class TileFarmNether extends TilePlanter {
	public TileFarmNether() {
		super("farmInfernal");
	}

	@Override
	public ItemStack[] createGermlingStacks() {
		return new ItemStack[]{
			new ItemStack(Items.NETHER_WART),
			new ItemStack(Items.NETHER_WART),
			new ItemStack(Items.NETHER_WART),
			new ItemStack(Items.NETHER_WART)
		};
	}

	@Override
	public ItemStack[] createResourceStacks() {
		return new ItemStack[]{
			new ItemStack(Blocks.SOUL_SAND),
			new ItemStack(Blocks.SOUL_SAND),
			new ItemStack(Blocks.SOUL_SAND),
			new ItemStack(Blocks.SOUL_SAND)
		};
	}

	@Override
	public ItemStack[] createProductionStacks() {
		return new ItemStack[]{
			new ItemStack(Items.NETHER_WART),
			new ItemStack(Items.NETHER_WART),
			new ItemStack(Items.NETHER_WART),
			new ItemStack(Items.NETHER_WART)
		};
	}
}

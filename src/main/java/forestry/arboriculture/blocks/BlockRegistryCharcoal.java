package forestry.arboriculture.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import forestry.api.core.ItemGroups;
import forestry.core.blocks.BlockRegistry;
import forestry.core.items.ItemBlockForestry;

public class BlockRegistryCharcoal extends BlockRegistry {
	public final BlockCharcoal charcoal;
	public final BlockWoodPile woodPile;
	public final BlockDecorativeWoodPile woodPileDecorative;
	public final BlockAsh ash;
	public final Block loam;

	public BlockRegistryCharcoal() {
		charcoal = new BlockCharcoal();
		ItemBlockForestry itemBlockCharcoal = new ItemBlockForestry<BlockCharcoal>(charcoal, new Item.Properties().group(ItemGroups.tabArboriculture)) {
			@Override
			public int getBurnTime(ItemStack itemStack) {
				return 16000;
			}
		};
		registerBlock(charcoal, itemBlockCharcoal, "charcoal");

		woodPile = new BlockWoodPile();
		ItemBlockForestry itemBlockWoodPile = new ItemBlockForestry<BlockWoodPile>(woodPile, new Item.Properties().group(ItemGroups.tabArboriculture)) {
			@Override
			public int getBurnTime(ItemStack itemStack) {
				return 1200;
			}
		};
		registerBlock(woodPile, itemBlockWoodPile, "wood_pile");

		woodPileDecorative = new BlockDecorativeWoodPile();
		ItemBlockForestry itemBlockWoodPileDecorative = new ItemBlockForestry<BlockDecorativeWoodPile>(woodPileDecorative, new Item.Properties().group(ItemGroups.tabArboriculture)) {
			@Override
			public int getBurnTime(ItemStack itemStack) {
				return 1200;
			}
		};
		registerBlock(woodPileDecorative, itemBlockWoodPileDecorative, "wood_pile_decorative");

		ash = new BlockAsh();
		registerBlock(ash, "ash_block");

		loam = new BlockLoam();
		registerBlock(loam, new ItemBlockForestry<>(loam, new Item.Properties().group(ItemGroups.tabArboriculture)), "loam");
	}

	public BlockState getAshState(int amount) {
		return ash.getDefaultState().with(BlockAsh.AMOUNT, Math.min(amount, 63));
	}
}

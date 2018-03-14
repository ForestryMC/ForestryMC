package forestry.arboriculture.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import net.minecraftforge.oredict.OreDictionary;

import forestry.core.blocks.BlockRegistry;
import forestry.core.items.ItemBlockForestry;
import forestry.core.utils.OreDictUtil;

public class BlockRegistryCharcoal extends BlockRegistry {
	public final BlockCharcoal charcoal;
	public final BlockWoodPile woodPile;
	public final BlockDecorativeWoodPile woodPileDecorative;
	public final BlockAsh ash;
	public final Block loam;

	public BlockRegistryCharcoal() {
		charcoal = new BlockCharcoal();
		ItemBlockForestry itemBlockCharcoal = new ItemBlockForestry<BlockCharcoal>(charcoal) {
			@Override
			public int getItemBurnTime(ItemStack itemStack) {
				return 16000;
			}
		};
		registerBlock(charcoal, itemBlockCharcoal, "charcoal");
		OreDictionary.registerOre(OreDictUtil.BLOCK_CHARCOAL, itemBlockCharcoal);

		woodPile = new BlockWoodPile();
		ItemBlockForestry itemBlockWoodPile = new ItemBlockForestry<BlockWoodPile>(woodPile) {
			@Override
			public int getItemBurnTime(ItemStack itemStack) {
				return 1200;
			}
		};
		registerBlock(woodPile, itemBlockWoodPile, "wood_pile");

		woodPileDecorative = new BlockDecorativeWoodPile();
		ItemBlockForestry itemBlockWoodPileDecorative = new ItemBlockForestry(woodPileDecorative) {
			@Override
			public int getItemBurnTime(ItemStack itemStack) {
				return 1200;
			}
		};
		registerBlock(woodPileDecorative, itemBlockWoodPileDecorative, "wood_pile_decorative");

		ash = new BlockAsh();
		registerBlock(ash, new ItemBlockForestry(ash), "ash_block");

		loam = new BlockLoam();
		registerBlock(loam, new ItemBlockForestry<>(loam), "loam");
	}
}

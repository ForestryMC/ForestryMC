package forestry.arboriculture.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

import net.minecraftforge.oredict.OreDictionary;

import forestry.core.blocks.BlockRegistry;
import forestry.core.items.ItemBlockForestry;
import forestry.core.utils.OreDictUtil;

public class BlockRegistryCharcoal extends BlockRegistry {
	public final BlockCharcoal charcoal;
	public final BlockWoodPile woodPile;
	public final BlockDecorativeWoodPile woodPileDecorative;
	public final BlockAsh[] ash = new BlockAsh[4];
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
		ItemBlockForestry itemBlockWoodPileDecorative = new ItemBlockForestry<BlockDecorativeWoodPile>(woodPileDecorative) {
			@Override
			public int getItemBurnTime(ItemStack itemStack) {
				return 1200;
			}
		};
		registerBlock(woodPileDecorative, itemBlockWoodPileDecorative, "wood_pile_decorative");

		for (int i = 0; i < 4; i++) {
			BlockAsh ashBlock = new BlockAsh(i * 16);
			ash[i] = ashBlock;
			registerBlock(ashBlock, new ItemBlockForestry<>(ashBlock), "ash_block_" + i);
		}

		loam = new BlockLoam();
		registerBlock(loam, new ItemBlockForestry<>(loam), "loam");
	}

	public IBlockState getAshState(int amount) {
		if (amount > 63) {
			amount = 63;
		}
		int i = amount / 16;
		return ash[i].getDefaultState().withProperty(BlockAsh.AMOUNT, amount % 16);
	}
}

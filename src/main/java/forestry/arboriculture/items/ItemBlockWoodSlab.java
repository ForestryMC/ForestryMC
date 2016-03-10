package forestry.arboriculture.items;

import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;

import forestry.api.arboriculture.EnumWoodType;
import forestry.arboriculture.WoodHelper;
import forestry.arboriculture.blocks.BlockArbSlab;

public class ItemBlockWoodSlab extends ItemSlab {
	public ItemBlockWoodSlab(BlockArbSlab block, BlockArbSlab slab, BlockArbSlab doubleSlab) {
		super(block, slab, doubleSlab);
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		BlockArbSlab wood = (BlockArbSlab) getBlock();
		int meta = itemstack.getMetadata();
		EnumWoodType woodType = wood.getWoodType(meta);
		return WoodHelper.getDisplayName(wood, woodType);
	}
}

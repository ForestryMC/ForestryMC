package forestry.core.items;

import forestry.core.gadgets.BlockSoil;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemSoilBlock extends ItemForestryBlock {

	public ItemSoilBlock(Block block) {
		super(block);
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		if (this.getBlock() instanceof BlockSoil) {
			BlockSoil.SoilType soilType = BlockSoil.getTypeFromMeta(itemstack.getItemDamage());
			return getBlock().getUnlocalizedName() + "." + soilType.ordinal();
		}
		return super.getUnlocalizedName(itemstack);
	}
}

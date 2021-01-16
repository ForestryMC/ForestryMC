package forestry.arboriculture.items;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import forestry.api.arboriculture.IWoodType;
import forestry.api.core.ItemGroups;
import forestry.arboriculture.WoodHelper;
import forestry.arboriculture.blocks.BlockForestrySlab;

public class ItemBlockWoodSlab extends BlockItem {

	public ItemBlockWoodSlab(BlockForestrySlab block) {
		super(block, new Item.Properties().group(ItemGroups.tabArboriculture));
	}

	@Override
	public ITextComponent getDisplayName(ItemStack itemstack) {
		BlockForestrySlab wood = (BlockForestrySlab) getBlock();
		IWoodType woodType = wood.getWoodType();
		return WoodHelper.getDisplayName(wood, woodType);
	}

	@Override
	public int getBurnTime(ItemStack itemStack) {
		BlockForestrySlab forestrySlab = (BlockForestrySlab) getBlock();
		if (forestrySlab.isFireproof()) {
			return 0;
		} else {
			return 150;
		}
	}
}

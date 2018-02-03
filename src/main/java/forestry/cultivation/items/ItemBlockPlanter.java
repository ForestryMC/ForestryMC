package forestry.cultivation.items;

import net.minecraft.item.ItemStack;

import forestry.core.items.ItemBlockForestry;
import forestry.core.utils.Translator;
import forestry.cultivation.blocks.BlockPlanter;

public class ItemBlockPlanter extends ItemBlockForestry<BlockPlanter> {

	public ItemBlockPlanter(BlockPlanter block) {
		super(block);
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return Translator.translateToLocalFormatted("tile.for.planter." + (BlockPlanter.isManual(stack) ? "manual" : "managed"), super.getItemStackDisplayName(stack));
	}
}

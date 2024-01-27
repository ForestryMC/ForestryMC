package forestry.cultivation.items;

import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;

import forestry.core.items.ItemBlockForestry;
import forestry.cultivation.blocks.BlockPlanter;

public class ItemBlockPlanter extends ItemBlockForestry<BlockPlanter> {

	public ItemBlockPlanter(BlockPlanter block) {
		super(block);
	}

	@Override
	public Component getName(ItemStack stack) {
		String name = getBlock().blockType.getSerializedName();
		return Component.translatable("block.forestry.planter." + (getBlock().getMode().getSerializedName()), Component.translatable("block.forestry." + name));
	}
}

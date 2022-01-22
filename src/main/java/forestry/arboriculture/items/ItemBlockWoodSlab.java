package forestry.arboriculture.items;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeType;

import forestry.api.arboriculture.IWoodType;
import forestry.api.core.ItemGroups;
import forestry.arboriculture.WoodHelper;
import forestry.arboriculture.blocks.BlockForestrySlab;

import org.jetbrains.annotations.Nullable;

public class ItemBlockWoodSlab extends BlockItem {

	public ItemBlockWoodSlab(BlockForestrySlab block) {
		super(block, new Item.Properties().tab(ItemGroups.tabArboriculture));
	}

	@Override
	public Component getName(ItemStack itemstack) {
		BlockForestrySlab wood = (BlockForestrySlab) getBlock();
		IWoodType woodType = wood.getWoodType();
		return WoodHelper.getDisplayName(wood, woodType);
	}

	@Override
	public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
		BlockForestrySlab forestrySlab = (BlockForestrySlab) getBlock();

		if (forestrySlab.isFireproof()) {
			return 0;
		} else {
			return 150;
		}
	}
}

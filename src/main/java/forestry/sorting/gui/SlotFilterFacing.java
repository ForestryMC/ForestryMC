package forestry.sorting.gui;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.render.TextureManagerForestry;

public class SlotFilterFacing extends Slot {

	public SlotFilterFacing(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public ResourceLocation getBackgroundLocation() {
		return TextureManagerForestry.getInstance().getGuiTextureMap();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public TextureAtlasSprite getBackgroundSprite() {
		return TextureManagerForestry.getInstance().getDefault("slots/bee");
	}
}

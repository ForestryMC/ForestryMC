/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.gui.slots;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.render.TextureManagerForestry;
import forestry.core.tiles.IFilterSlotDelegate;

/**
 * Slot which only takes specific items, specified by the IFilterSlotDelegate.
 */
public class SlotFiltered extends SlotWatched {
	private final IFilterSlotDelegate filterSlotDelegate;
	@Nullable
	private String backgroundTexture = null;
	private String blockedTexture = "slots/blocked";

	public <T extends IInventory & IFilterSlotDelegate> SlotFiltered(T inventory, int slotIndex, int xPos, int yPos) {
		super(inventory, slotIndex, xPos, yPos);
		this.filterSlotDelegate = inventory;
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		int slotIndex = getSlotIndex();
		return !filterSlotDelegate.isLocked(slotIndex) &&
			(itemstack.isEmpty() || filterSlotDelegate.canSlotAccept(slotIndex, itemstack));
	}

	public SlotFiltered setBlockedTexture(String ident) {
		blockedTexture = ident;
		return this;
	}

	public SlotFiltered setBackgroundTexture(String backgroundTexture) {
		this.backgroundTexture = backgroundTexture;
		return this;
	}

	@SideOnly(Side.CLIENT)
	@Override
	@Nullable
	public TextureAtlasSprite getBackgroundSprite() {
		ItemStack stack = getStack();
		if (!isItemValid(stack)) {
			return TextureManagerForestry.getInstance().getDefault(blockedTexture);
		} else if (backgroundTexture != null) {
			return TextureManagerForestry.getInstance().getDefault(backgroundTexture);
		} else {
			return null;
		}
	}
}

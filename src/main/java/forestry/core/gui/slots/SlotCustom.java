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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.interfaces.ICrafter;
import forestry.core.render.TextureManager;
import forestry.core.utils.StackUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

/**
 * Slot which only specific items. (With permission by RichardG.)
 *
 * @author Richard
 */
public class SlotCustom extends SlotForestry {

	protected final Object[] items;
	private boolean exclusion;
	private ICrafter crafter;
	private String blockedTexture = "slots/blocked";

	public SlotCustom(IInventory iinventory, int slotIndex, int xPos, int yPos, Object... items) {
		super(iinventory, slotIndex, xPos, yPos);
		this.items = items;
	}

	public SlotCustom setCrafter(ICrafter crafter) {
		this.crafter = crafter;
		return this;
	}

	public SlotCustom setExclusion(boolean exclusion) {
		this.exclusion = exclusion;
		return this;
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		if (itemstack == null)
			return false;
		if (!inventory.isItemValidForSlot(getSlotIndex(), itemstack))
			return false;

		if (exclusion)
			return !determineValidity(itemstack);
		else
			return determineValidity(itemstack);
	}

	@Override
	public boolean getHasStack() {
		if (crafter != null && !crafter.canTakeStack(getSlotIndex()))
			return false;
		else
			return super.getHasStack();
	}

	@Override
	public ItemStack decrStackSize(int i) {
		if (crafter != null && !crafter.canTakeStack(getSlotIndex()))
			return null;
		else
			return super.decrStackSize(i);
	}

	@Override
	public void onPickupFromSlot(EntityPlayer player, ItemStack itemStack) {
		if (crafter != null)
			crafter.takenFromSlot(getSlotIndex(), true, player);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean determineValidity(ItemStack itemstack) {
		for (Object filter : items) {
			if (filter == null)
				continue;
			if (filter instanceof Class) {
				Block block = StackUtils.getBlock(itemstack);

				if (block != null && ((Class) filter).isAssignableFrom(block.getClass())) return true;
				if (((Class) filter).isAssignableFrom(itemstack.getItem().getClass())) return true;
			} else if (filter instanceof ItemStack) {
				if (((ItemStack) filter).getItemDamage() == Defaults.WILDCARD && itemstack.getItem() == ((ItemStack) filter).getItem())
					return true;
				else if (itemstack.isItemEqual((ItemStack) filter))
					return true;
			} else if (filter instanceof Block) {
				if (StackUtils.equals((Block) filter, itemstack)) return true;
			} else if (filter instanceof Item) {
				if (itemstack.getItem() == filter) return true;
			} else if (filter instanceof ForestryItem) {
				if (((ForestryItem) filter).isItemEqual(itemstack)) return true;
			} else if (filter instanceof ForestryBlock) {
				if (((ForestryBlock) filter).isItemEqual(itemstack)) return true;
			} else if (filter instanceof ISpeciesRoot) {
				if (((ISpeciesRoot) filter).isMember(itemstack)) return true;
			} else {
				throw new RuntimeException("invalid filter item specified: "+filter+" ("+filter.getClass()+")");
			}
		}

		return false;
	}

	public SlotCustom setBlockedTexture(String ident) {
		blockedTexture = ident;
		return this;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getBackgroundIconIndex() {
		ItemStack stack = getStack();
		if (stack != null && !inventory.isItemValidForSlot(getSlotIndex(), stack))
			return TextureManager.getInstance().getDefault(blockedTexture);
		else
			return null;
	}
}

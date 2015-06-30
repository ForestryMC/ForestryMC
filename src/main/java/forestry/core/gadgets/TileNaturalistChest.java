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
package forestry.core.gadgets;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import forestry.api.core.ForestryAPI;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.GuiHandler;
import forestry.core.config.Config;
import forestry.core.gui.IPagedInventory;
import forestry.core.inventory.TileInventoryAdapter;

public abstract class TileNaturalistChest extends TileBase implements IPagedInventory {
	private final int guiID;

	public TileNaturalistChest(ISpeciesRoot speciesRoot, int guiId) {
		setInternalInventory(new NaturalistInventoryAdapter(this, 125, "Items", speciesRoot));
		setHints(Config.hints.get("apiarist.chest"));
		this.guiID = guiId;
	}

	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		player.openGui(ForestryAPI.instance, guiID, player.worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	public void flipPage(EntityPlayer player, int page) {
		player.openGui(ForestryAPI.instance, GuiHandler.encodeGuiData(guiID, page), player.worldObj, xCoord, yCoord, zCoord);
	}

	private static class NaturalistInventoryAdapter extends TileInventoryAdapter<TileNaturalistChest> {
		private final ISpeciesRoot speciesRoot;

		public NaturalistInventoryAdapter(TileNaturalistChest tile, int size, String name, ISpeciesRoot speciesRoot) {
			super(tile, size, name);
			this.speciesRoot = speciesRoot;
		}

		@Override
		public boolean canSlotAccept(int slotIndex, ItemStack itemstack) {
			return speciesRoot.isMember(itemstack);
		}

		@Override
		public boolean canExtractItem(int slotIndex, ItemStack stack, int side) {
			return true;
		}
	}
}

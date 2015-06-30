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
package forestry.apiculture.multiblock;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import forestry.api.apiculture.DefaultBeeListener;
import forestry.api.apiculture.IAlvearyComponent;
import forestry.api.apiculture.IBeeListener;
import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.ISpeciesRoot;
import forestry.apiculture.gadgets.BlockAlveary;
import forestry.core.config.ForestryItem;
import forestry.core.interfaces.ICrafter;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.inventory.TileInventoryAdapter;
import forestry.core.network.GuiId;
import forestry.core.utils.StackUtils;

public class TileAlvearySieve extends TileAlvearyWithGui implements ICrafter, IAlvearyComponent.BeeListener {

	private final IBeeListener beeListener;
	private final AlvearySieveInventory inventory;

	public TileAlvearySieve() {
		super(TileAlveary.SIEVE_META, GuiId.AlvearySieveGUI);
		this.inventory = new AlvearySieveInventory(this);
		this.beeListener = new AlvearySieveBeeListener(this);
	}

	@Override
	public IInventoryAdapter getInternalInventory() {
		return inventory;
	}

	@Override
	public IBeeListener getBeeListener() {
		return beeListener;
	}
	
	/* TEXTURES & INTERNAL */
	@Override
	public int getIcon(int side, int metadata) {
		if (side == 0 || side == 1) {
			return BlockAlveary.BOTTOM;
		}
		return BlockAlveary.SIEVE;
	}

	/* ICRAFTER */
	@Override
	public boolean canTakeStack(int slotIndex) {
		return true;
	}

	@Override
	public ItemStack takenFromSlot(int slotIndex, EntityPlayer player) {
		return inventory.takenFromSlot(slotIndex);
	}

	@Override
	public ItemStack getResult() {
		return null;
	}

	private static class AlvearySieveBeeListener extends DefaultBeeListener {
		private final TileAlvearySieve tile;

		public AlvearySieveBeeListener(TileAlvearySieve tile) {
			this.tile = tile;
		}

		@Override
		public boolean onPollenRetrieved(IIndividual pollen) {
			if (!tile.inventory.canStorePollen()) {
				return false;
			}

			ISpeciesRoot speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(pollen.getClass());

			ItemStack pollenStack = speciesRoot.getMemberStack(pollen, EnumGermlingType.POLLEN.ordinal());
			if (pollenStack != null) {
				tile.inventory.storePollenStack(pollenStack);
				return true;
			}
			return false;
		}
	}

	public static class AlvearySieveInventory extends TileInventoryAdapter<TileAlvearySieve> {
		public static final int SLOT_POLLEN_1 = 0;
		public static final int SLOTS_POLLEN_COUNT = 4;
		public static final int SLOT_SIEVE = 4;

		public AlvearySieveInventory(TileAlvearySieve alvearySieve) {
			super(alvearySieve, 5, "Items", 1);
		}

		@Override
		public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
			return StackUtils.isIdenticalItem(ForestryItem.craftingMaterial.getItemStack(1, 3), itemStack);
		}

		private boolean canStorePollen() {
			if (getStackInSlot(SLOT_SIEVE) == null) {
				return false;
			}

			for (int i = SLOT_POLLEN_1; i < SLOT_POLLEN_1 + SLOTS_POLLEN_COUNT; i++) {
				if (getStackInSlot(i) == null) {
					return true;
				}
			}

			return false;
		}

		private void storePollenStack(ItemStack itemstack) {
			for (int i = SLOT_POLLEN_1; i < SLOT_POLLEN_1 + SLOTS_POLLEN_COUNT; i++) {
				if (getStackInSlot(i) == null) {
					setInventorySlotContents(i, itemstack);
					return;
				}
			}
		}

		public ItemStack takenFromSlot(int slotIndex) {
			if (slotIndex == SLOT_SIEVE) {
				for (int i = SLOT_POLLEN_1; i < SLOT_POLLEN_1 + SLOTS_POLLEN_COUNT; i++) {
					setInventorySlotContents(i, null);
				}
				return getStackInSlot(SLOT_SIEVE);
			} else {
				setInventorySlotContents(SLOT_SIEVE, null);
				return getStackInSlot(slotIndex);
			}
		}
	}

}

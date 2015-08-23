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
package forestry.apiculture.gadgets;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import forestry.api.apiculture.IAlvearyComponent;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeListener;
import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.core.ForestryAPI;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IIndividual;
import forestry.core.config.ForestryItem;
import forestry.core.interfaces.ICrafter;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.inventory.TileInventoryAdapter;
import forestry.core.network.GuiId;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StackUtils;

public class TileAlvearySieve extends TileAlveary implements ICrafter, IBeeListener {

	/* CONSTANTS */
	public static final int BLOCK_META = 7;
	
	public static final int SLOT_POLLEN_1 = 0;
	public static final int SLOTS_POLLEN_COUNT = 4;
	public static final int SLOT_SIEVE = 4;

	public TileAlvearySieve() {
		super(BLOCK_META);
		setInternalInventory(new TileInventoryAdapter(this, 5, "Items", 1) {
			@Override
			public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
				return StackUtils.isIdenticalItem(ForestryItem.craftingMaterial.getItemStack(1, 3), itemStack);
			}
		});
	}

	@Override
	public void openGui(EntityPlayer player) {
		player.openGui(ForestryAPI.instance, GuiId.AlvearySieveGUI.ordinal(), worldObj, pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	public boolean hasFunction() {
		return true;
	}
	
	/* UPDATING */
	@Override
	public void initialize() {
		super.initialize();
		
		if (!hasMaster() || !isIntegratedIntoStructure() || !Proxies.common.isSimulating(worldObj)) {
			return;
		}
		
		((IAlvearyComponent) getCentralTE()).registerBeeListener(this);
	}
	
	@Override
	protected void updateServerSide() {
		super.updateServerSide();
		if (!updateOnInterval(200)) {
			return;
		}
		
		if (!hasMaster() || !isIntegratedIntoStructure()) {
			return;
		}
		
		((IAlvearyComponent) getCentralTE()).registerBeeListener(this);
	}
	
	/* TEXTURES & INTERNAL */
	@Override
	public int getIcon(int side, int metadata) {
		if (side == 0 || side == 1) {
			return BlockAlveary.BOTTOM;
		}
		return BlockAlveary.SIEVE;
	}

	private void destroySieve() {
		IInventoryAdapter inventory = getInternalInventory();
		inventory.setInventorySlotContents(SLOT_SIEVE, null);
	}

	private void destroyPollen() {
		IInventoryAdapter inventory = getInternalInventory();
		for (int i = SLOT_POLLEN_1; i < SLOT_POLLEN_1 + SLOTS_POLLEN_COUNT; i++) {
			inventory.setInventorySlotContents(i, null);
		}
	}
	
	private boolean canStorePollen() {
		IInventoryAdapter inventory = getInternalInventory();
		if (inventory.getStackInSlot(SLOT_SIEVE) == null) {
			return false;
		}
		
		for (int i = SLOT_POLLEN_1; i < SLOT_POLLEN_1 + SLOTS_POLLEN_COUNT; i++) {
			if (inventory.getStackInSlot(i) == null) {
				return true;
			}
		}
		
		return false;
	}
	
	private void storePollenStack(ItemStack itemstack) {
		IInventoryAdapter inventory = getInternalInventory();
		for (int i = SLOT_POLLEN_1; i < SLOT_POLLEN_1 + SLOTS_POLLEN_COUNT; i++) {
			if (inventory.getStackInSlot(i) == null) {
				inventory.setInventorySlotContents(i, itemstack);
				return;
			}
		}
	}

	/* ICRAFTER */
	@Override
	public boolean canTakeStack(int slotIndex) {
		return true;
	}

	@Override
	public ItemStack takenFromSlot(int slotIndex, boolean consumeRecipe, EntityPlayer player) {
		IInventoryAdapter inventory = getInternalInventory();
		if (slotIndex == SLOT_SIEVE) {
			destroyPollen();
			return inventory.getStackInSlot(SLOT_SIEVE);
		} else {
			destroySieve();
			return inventory.getStackInSlot(slotIndex);
		}
	}

	@Override
	public ItemStack getResult() {
		return null;
	}

	/* IBEELISTENER */
	@Override
	public void onQueenChange(ItemStack queen) {
	}

	@Override
	public void wearOutEquipment(int amount) {
	}

	@Override
	public void onQueenDeath(IBee queen) {
	}

	@Override
	public void onPostQueenDeath(IBee queen) {
	}

	@Override
	public boolean onPollenRetrieved(IBee queen, IIndividual pollen, boolean isHandled) {

		if (isHandled) {
			return true;
		}
		if (!canStorePollen()) {
			return false;
		}
		
		storePollenStack(AlleleManager.alleleRegistry.getSpeciesRoot(pollen.getClass()).getMemberStack(pollen, EnumGermlingType.POLLEN.ordinal()));
		return true;
	}

	@Override
	public boolean onEggLaid(IBee queen) {
		return false;
	}
}

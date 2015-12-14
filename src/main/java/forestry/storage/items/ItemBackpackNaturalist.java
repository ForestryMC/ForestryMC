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
package forestry.storage.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import forestry.api.genetics.ISpeciesRoot;
import forestry.api.storage.EnumBackpackType;
import forestry.api.storage.IBackpackDefinition;
import forestry.core.config.Constants;
import forestry.core.gui.GuiHandler;
import forestry.core.gui.GuiNaturalistInventory;
import forestry.storage.gui.ContainerNaturalistBackpack;
import forestry.storage.inventory.ItemInventoryBackpackPaged;

public class ItemBackpackNaturalist extends ItemBackpack {
	private final ISpeciesRoot speciesRoot;

	public ItemBackpackNaturalist(ISpeciesRoot speciesRoot, IBackpackDefinition definition, EnumBackpackType type) {
		super(definition, type);
		this.speciesRoot = speciesRoot;
	}

	@Override
	protected void openGui(EntityPlayer entityplayer) {
		GuiHandler.openGui(entityplayer, this);
	}

	@Override
	public Object getGui(EntityPlayer player, ItemStack heldItem, int page) {
		ItemInventoryBackpackPaged inventory = new ItemInventoryBackpackPaged(player, Constants.SLOTS_BACKPACK_APIARIST, heldItem, this);
		ContainerNaturalistBackpack container = new ContainerNaturalistBackpack(player, inventory, page);
		return new GuiNaturalistInventory(speciesRoot, player, container, inventory, page, 5);
	}

	@Override
	public Object getContainer(EntityPlayer player, ItemStack heldItem, int page) {
		ItemInventoryBackpackPaged inventory = new ItemInventoryBackpackPaged(player, Constants.SLOTS_BACKPACK_APIARIST, heldItem, this);
		return new ContainerNaturalistBackpack(player, inventory, page);
	}
}

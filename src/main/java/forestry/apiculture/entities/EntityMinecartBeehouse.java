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
package forestry.apiculture.entities;

import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import forestry.api.apiculture.DefaultBeeListener;
import forestry.api.apiculture.IBeeHousingInventory;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.apiculture.BeehouseBeeModifier;
import forestry.apiculture.InventoryBeeHousing;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.network.GuiId;

public class EntityMinecartBeehouse extends EntityMinecartBeeHousingBase {
	private static final IBeeModifier beeModifier = new BeehouseBeeModifier();
	private static final IBeeListener beeListener = new DefaultBeeListener();
	private final InventoryBeeHousing beeInventory = new InventoryBeeHousing(9, "Items", getAccessHandler());

	@SuppressWarnings("unused")
	public EntityMinecartBeehouse(World world) {
		super(world);
		beeInventory.disableAutomation();
	}

	public EntityMinecartBeehouse(World world, double posX, double posY, double posZ) {
		super(world, posX, posY, posZ);
		beeInventory.disableAutomation();
	}

	@Override
	protected GuiId getGuiId() {
		return GuiId.MinecartBeehouseGUI;
	}

	@Override
	public List<String> getHints() {
		return Config.hints.get("bee.house");
	}

	@Override
	public Block func_145820_n() {
		return ForestryBlock.apiculture.block();
	}

	@Override
	public int getDisplayTileData() {
		return Constants.DEFINITION_BEEHOUSE_META;
	}

	@Override
	public ItemStack getCartItem() {
		return ForestryItem.minecartBeehouse.getItemStack(1, 0);
	}

	/* IBeeHousing */
	@Override
	public Iterable<IBeeModifier> getBeeModifiers() {
		return Collections.singleton(beeModifier);
	}

	@Override
	public Iterable<IBeeListener> getBeeListeners() {
		return Collections.singleton(beeListener);
	}

	@Override
	public IBeeHousingInventory getBeeInventory() {
		return beeInventory;
	}

	@Override
	protected IInventoryAdapter getInternalInventory() {
		return beeInventory;
	}
}

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import forestry.api.apiculture.IBeeHousingInventory;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IHiveFrame;
import forestry.apiculture.ApiaryBeeListener;
import forestry.apiculture.ApiaryBeeModifier;
import forestry.apiculture.IApiary;
import forestry.apiculture.blocks.BlockApicultureType;
import forestry.apiculture.gui.ContainerMinecartBeehouse;
import forestry.apiculture.gui.GuiBeeHousing;
import forestry.apiculture.inventory.IApiaryInventory;
import forestry.apiculture.inventory.InventoryApiary;
import forestry.core.config.Config;
import forestry.core.inventory.IInventoryAdapter;
import forestry.plugins.PluginApiculture;

public class EntityMinecartApiary extends EntityMinecartBeeHousingBase implements IApiary {
	private static final IBeeModifier beeModifier = new ApiaryBeeModifier();
	private final IBeeListener beeListener = new ApiaryBeeListener(this);
	private final InventoryApiary inventory = new InventoryApiary(getAccessHandler());

	@SuppressWarnings("unused")
	public EntityMinecartApiary(World world) {
		super(world);
	}

	public EntityMinecartApiary(World world, double posX, double posY, double posZ) {
		super(world, posX, posY, posZ);
	}

	@Override
	public List<String> getHints() {
		return Config.hints.get("apiary");
	}

	@Override
	protected IInventoryAdapter getInternalInventory() {
		return inventory;
	}

	@Override
	public IApiaryInventory getApiaryInventory() {
		return inventory;
	}

	@Override
	public IBeeHousingInventory getBeeInventory() {
		return inventory;
	}

	@Override
	public Block func_145820_n() {
		return PluginApiculture.blocks.apiculture;
	}

	@Override
	public int getDisplayTileData() {
		return BlockApicultureType.APIARY.ordinal();
	}

	@Override
	public ItemStack getCartItem() {
		return PluginApiculture.items.minecartBeehouse.getApiaryMinecart();
	}

	@Override
	public Collection<IBeeModifier> getBeeModifiers() {
		List<IBeeModifier> beeModifiers = new ArrayList<>();

		beeModifiers.add(beeModifier);

		for (IHiveFrame frame : inventory.getFrames()) {
			beeModifiers.add(frame.getBeeModifier());
		}

		return beeModifiers;
	}

	@Override
	public Iterable<IBeeListener> getBeeListeners() {
		return Collections.singleton(beeListener);
	}

	@Override
	public Object getGui(EntityPlayer player, int data) {
		ContainerMinecartBeehouse container = new ContainerMinecartBeehouse(player.inventory, this, true);
		return new GuiBeeHousing<>(this, container, GuiBeeHousing.Icon.APIARY);
	}

	@Override
	public Object getContainer(EntityPlayer player, int data) {
		return new ContainerMinecartBeehouse(player.inventory, this, true);
	}

	@Override
	public int getIdOfEntity() {
		return getEntityId();
	}
}

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

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.apiculture.IBeeHousingInventory;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IHiveFrame;
import forestry.apiculture.ApiaryBeeListener;
import forestry.apiculture.ApiaryBeeModifier;
import forestry.apiculture.IApiary;
import forestry.apiculture.ModuleApiculture;
import forestry.apiculture.gui.ContainerMinecartBeehouse;
import forestry.apiculture.gui.GuiBeeHousing;
import forestry.apiculture.inventory.IApiaryInventory;
import forestry.apiculture.inventory.InventoryApiary;
import forestry.core.inventory.IInventoryAdapter;

public class EntityMinecartApiary extends EntityMinecartBeeHousingBase implements IApiary {

	private static final IBeeModifier beeModifier = new ApiaryBeeModifier();

	private final IBeeListener beeListener = new ApiaryBeeListener(this);
	private final InventoryApiary inventory = new InventoryApiary();

	@SuppressWarnings("unused")
	public EntityMinecartApiary(World world) {
		super(world);
	}

	public EntityMinecartApiary(World world, double posX, double posY, double posZ) {
		super(world, posX, posY, posZ);
	}

	@Override
	public String getHintKey() {
		return "apiary";
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
	public IBlockState getDisplayTile() {
		return ModuleApiculture.getBlocks().apiary.getDefaultState();
	}

	@Override
	public ItemStack getCartItem() {
		return ModuleApiculture.getItems().minecartBeehouse.getApiaryMinecart();
	}

	@Override
	public Collection<IBeeModifier> getBeeModifiers() {
		List<IBeeModifier> beeModifiers = new ArrayList<>();

		beeModifiers.add(beeModifier);

		for (Tuple<IHiveFrame, ItemStack> frame : inventory.getFrames()) {
			IHiveFrame hiveFrame = frame.getFirst();
			ItemStack stack = frame.getSecond();
			IBeeModifier beeModifier = hiveFrame.getBeeModifier(stack);
			beeModifiers.add(beeModifier);
		}

		return beeModifiers;
	}

	@Override
	public Iterable<IBeeListener> getBeeListeners() {
		return Collections.singleton(beeListener);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer getGui(EntityPlayer player, int data) {
		ContainerMinecartBeehouse container = new ContainerMinecartBeehouse(player.inventory, this, true);
		return new GuiBeeHousing<>(this, container, GuiBeeHousing.Icon.APIARY);
	}

	@Override
	public Container getContainer(EntityPlayer player, int data) {
		return new ContainerMinecartBeehouse(player.inventory, this, true);
	}
}

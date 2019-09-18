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

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;

import net.minecraftforge.fml.network.NetworkHooks;

import forestry.api.apiculture.IBeeHousingInventory;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.hives.IHiveFrame;
import forestry.apiculture.ApiaryBeeListener;
import forestry.apiculture.ApiaryBeeModifier;
import forestry.apiculture.IApiary;
import forestry.apiculture.blocks.BlockTypeApiculture;
import forestry.apiculture.features.ApicultureBlocks;
import forestry.apiculture.features.ApicultureItems;
import forestry.apiculture.gui.ContainerMinecartBeehouse;
import forestry.apiculture.gui.GuiBeeHousing;
import forestry.apiculture.inventory.IApiaryInventory;
import forestry.apiculture.inventory.InventoryApiary;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.network.PacketBufferForestry;

public class MinecartEntityApiary extends MinecartEntityBeeHousingBase implements IApiary {

	private static final IBeeModifier beeModifier = new ApiaryBeeModifier();

	private final IBeeListener beeListener = new ApiaryBeeListener(this);
	private final InventoryApiary inventory = new InventoryApiary();

	@SuppressWarnings("unused")
	public MinecartEntityApiary(World world) {
		super(world);
	}

	public MinecartEntityApiary(World world, double posX, double posY, double posZ) {
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
	public BlockState getDisplayTile() {
		return ApicultureBlocks.BASE.get(BlockTypeApiculture.APIARY).defaultState();
	}

	@Override
	public ItemStack getCartItem() {
		return ApicultureItems.MINECART_BEEHOUSE.item().getApiaryMinecart();
	}

	@Override
	public Collection<IBeeModifier> getBeeModifiers() {
		List<IBeeModifier> beeModifiers = new ArrayList<>();

		beeModifiers.add(beeModifier);

		for (Tuple<IHiveFrame, ItemStack> frame : inventory.getFrames()) {
			IHiveFrame hiveFrame = frame.getA();
			ItemStack stack = frame.getB();
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
	public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
		return new ContainerMinecartBeehouse(windowId, player.inventory, this, true, GuiBeeHousing.Icon.APIARY);
	}

	//TODO - check
	@Override
	public boolean processInitialInteract(PlayerEntity player, Hand hand) {
		if (super.processInitialInteract(player, hand)) {
			return true;
		}
		//TODO sides
		NetworkHooks.openGui((ServerPlayerEntity) player, this, p -> {
			PacketBufferForestry fP = new PacketBufferForestry(p);
			fP.writeEntityById(getEntity());
			fP.writeBoolean(true);
			fP.writeEnum(GuiBeeHousing.Icon.APIARY, GuiBeeHousing.Icon.values());
		});
		return true;
	}
}

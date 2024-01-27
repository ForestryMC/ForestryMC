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
package forestry.apiculture.tiles;

import java.util.Collections;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.network.NetworkHooks;

import forestry.api.apiculture.DefaultBeeListener;
import forestry.api.apiculture.IBeeHousingInventory;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.apiculture.BeehouseBeeModifier;
import forestry.apiculture.InventoryBeeHousing;
import forestry.apiculture.features.ApicultureTiles;
import forestry.apiculture.gui.ContainerBeeHousing;
import forestry.apiculture.gui.GuiBeeHousing;
import forestry.core.network.PacketBufferForestry;

public class TileBeeHouse extends TileBeeHousingBase {
	private static final IBeeModifier beeModifier = new BeehouseBeeModifier();

	private final IBeeListener beeListener;
	private final InventoryBeeHousing beeInventory;

	public TileBeeHouse(BlockPos pos, BlockState state) {
		super(ApicultureTiles.BEE_HOUSE.tileType(), pos, state, "bee.house");
		this.beeListener = new DefaultBeeListener();

		beeInventory = new InventoryBeeHousing(12);
		beeInventory.disableAutomation();
		setInternalInventory(beeInventory);
	}

	@Override
	public IBeeHousingInventory getBeeInventory() {
		return beeInventory;
	}

	@Override
	public Iterable<IBeeModifier> getBeeModifiers() {
		return Collections.singleton(beeModifier);
	}

	@Override
	public Iterable<IBeeListener> getBeeListeners() {
		return Collections.singleton(beeListener);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new ContainerBeeHousing(windowId, player.getInventory(), this, false, GuiBeeHousing.Icon.BEE_HOUSE);
	}

	@Override
	public void openGui(ServerPlayer player, BlockPos pos) {
		NetworkHooks.openScreen(player, this, p -> {
			PacketBufferForestry forestryP = new PacketBufferForestry(p);
			forestryP.writeBlockPos(pos);
			forestryP.writeBoolean(false);
			forestryP.writeEnum(GuiBeeHousing.Icon.BEE_HOUSE, GuiBeeHousing.Icon.values());
		});
	}
}

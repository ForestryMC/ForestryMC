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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.network.NetworkHooks;

import forestry.api.apiculture.IBeeHousingInventory;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.hives.IHiveFrame;
import forestry.apiculture.ApiaryBeeListener;
import forestry.apiculture.ApiaryBeeModifier;
import forestry.apiculture.IApiary;
import forestry.apiculture.features.ApicultureTiles;
import forestry.apiculture.gui.ContainerBeeHousing;
import forestry.apiculture.gui.GuiBeeHousing;
import forestry.apiculture.inventory.IApiaryInventory;
import forestry.apiculture.inventory.InventoryApiary;
import forestry.core.network.PacketBufferForestry;

public class TileApiary extends TileBeeHousingBase implements IApiary {
	private final IBeeModifier beeModifier = new ApiaryBeeModifier();
	private final IBeeListener beeListener = new ApiaryBeeListener(this);
	private final InventoryApiary inventory = new InventoryApiary();

	public TileApiary(BlockPos pos, BlockState state) {
		super(ApicultureTiles.APIARY.tileType(), pos, state, "apiary");
		setInternalInventory(inventory);
	}

	@Override
	public IBeeHousingInventory getBeeInventory() {
		return inventory;
	}

	@Override
	public IApiaryInventory getApiaryInventory() {
		return inventory;
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
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new ContainerBeeHousing(windowId, player.getInventory(), this, true, GuiBeeHousing.Icon.APIARY);
	}

	@Override
	public void openGui(ServerPlayer player, BlockPos pos) {
		NetworkHooks.openScreen(player, this, p -> {
			PacketBufferForestry forestryP = new PacketBufferForestry(p);
			forestryP.writeBlockPos(pos);
			forestryP.writeBoolean(true);
			forestryP.writeEnum(GuiBeeHousing.Icon.APIARY, GuiBeeHousing.Icon.values());
		});
	}
}

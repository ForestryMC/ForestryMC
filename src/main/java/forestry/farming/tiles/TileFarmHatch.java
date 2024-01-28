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
package forestry.farming.tiles;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import forestry.api.multiblock.IFarmComponent;
import forestry.core.inventory.AdjacentInventoryCache;
import forestry.core.tiles.AdjacentTileCache;
import forestry.core.utils.InventoryUtil;
import forestry.farming.features.FarmingTiles;

public class TileFarmHatch extends TileFarm implements WorldlyContainer, IFarmComponent.Active {

	private static final Direction[] dumpDirections = new Direction[]{Direction.DOWN};

	private final AdjacentTileCache tileCache;
	private final AdjacentInventoryCache inventoryCache;

	public TileFarmHatch(BlockPos pos, BlockState state) {
		super(FarmingTiles.HATCH.tileType(), pos, state);
		this.tileCache = new AdjacentTileCache(this);
		this.inventoryCache = new AdjacentInventoryCache(this, tileCache, tile -> !(tile instanceof TileFarm) && tile.getBlockPos().getY() < getBlockPos().getY());
	}

	@Override
	public boolean allowsAutomation() {
		return true;
	}

	@Override
	public void updateServer(int tickCount) {
		if (tickCount % 40 == 0) {
			Container productInventory = getMultiblockLogic().getController().getFarmInventory().getProductInventory();
			IItemHandler productItemHandler = new InvWrapper(productInventory);

            InventoryUtil.moveItemStack(productItemHandler, inventoryCache.getAdjacentInventories());
        }
	}

	@Override
	public void updateClient(int tickCount) {

	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		if (capability == ForgeCapabilities.ITEM_HANDLER) {
			SidedInvWrapper sidedInvWrapper = new SidedInvWrapper(this, facing);
			return LazyOptional.of(() -> sidedInvWrapper).cast();
		}
		return super.getCapability(capability, facing);
	}
}

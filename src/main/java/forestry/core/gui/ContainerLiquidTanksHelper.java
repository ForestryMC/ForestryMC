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
package forestry.core.gui;

import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import forestry.api.core.IToolPipette;
import forestry.core.fluids.StandardTank;
import forestry.core.network.packets.PacketPipetteClick;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.utils.NetworkUtil;

public class ContainerLiquidTanksHelper<T extends BlockEntity & ILiquidTankTile> implements IContainerLiquidTanks {

	private final T tile;

	public ContainerLiquidTanksHelper(T tile) {
		this.tile = tile;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void handlePipetteClickClient(int slot, Player player) {
		ItemStack itemstack = player.inventoryMenu.getCarried();
		if (itemstack.getItem() instanceof IToolPipette) {
			NetworkUtil.sendToServer(new PacketPipetteClick(slot));
		}
	}

	@Override
	public void handlePipetteClick(int slot, ServerPlayer player) {
		ItemStack itemstack = player.inventoryMenu.getCarried();
		Item held = itemstack.getItem();
		if (!(held instanceof IToolPipette pipette)) {
			return;
		}

		IFluidTank tank = tile.getTankManager().getTank(slot);
		int liquidAmount = tank.getFluidAmount();

		LazyOptional<IFluidHandlerItem> fluidCap = FluidUtil.getFluidHandler(itemstack);
		fluidCap.ifPresent(fluidHandlerItem -> {
			if (pipette.canPipette(itemstack) && liquidAmount > 0) {
				if (tank instanceof StandardTank) {
					FluidStack fillAmount = ((StandardTank) tank).drainInternal(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
					int filled = fluidHandlerItem.fill(fillAmount, IFluidHandler.FluidAction.EXECUTE);
					tank.drain(filled, IFluidHandler.FluidAction.EXECUTE);
					player.inventoryMenu.setCarried(fluidHandlerItem.getContainer());
					player.inventoryMenu.broadcastChanges();
				} else {//TODO: Test if this works
					FluidStack fillAmount = tank.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
					int filled = fluidHandlerItem.fill(fillAmount, IFluidHandler.FluidAction.EXECUTE);
					tank.drain(filled, IFluidHandler.FluidAction.EXECUTE);
					player.inventoryMenu.setCarried(fluidHandlerItem.getContainer());
					player.inventoryMenu.broadcastChanges();
				}
			} else {
				FluidStack potential = fluidHandlerItem.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
				if (!potential.isEmpty()) {
					if (tank instanceof FluidTank) {
						int fill = tank.fill(potential, IFluidHandler.FluidAction.EXECUTE);
						fluidHandlerItem.drain(fill, IFluidHandler.FluidAction.EXECUTE);
						player.inventoryMenu.setCarried(fluidHandlerItem.getContainer());
						player.inventoryMenu.broadcastChanges();
					}
				}
			}
		});
	}

	@Override
	public IFluidTank getTank(int slot) {
		return tile.getTankManager().getTank(slot);
	}
}

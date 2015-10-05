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
package forestry.core.gadgets;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import forestry.core.circuits.ISocketable;
import forestry.core.multiblock.MultiblockControllerBase;
import forestry.core.multiblock.MultiblockTileEntityBase;
import forestry.core.proxy.Proxies;
import forestry.core.utils.Utils;

public abstract class BlockStructure extends BlockForestry {

	protected BlockStructure(Material material) {
		super(material);
		setHardness(1.0f);
	}

	@Override
	public boolean canSilkHarvest() {
		return false;
	}

	private long previousMessageTick = 0;
	
	@SuppressWarnings("unused")
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (player.isSneaking()) {
			return false;
		}

		MultiblockTileEntityBase part;
		MultiblockControllerBase controller;

		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof MultiblockTileEntityBase) {
			part = (MultiblockTileEntityBase) tile;
			controller = part.getMultiblockController();
		} else {
			return false;
		}

		// If the player's hands are empty and they right-click on a multiblock, they get a
		// multiblock-debugging message if the machine is not assembled.
		if (player.getCurrentEquippedItem() == null && !controller.isAssembled()) {
			if (controller != null) {
				Exception e = controller.getLastValidationException();
				if (e != null) {
					long tick = world.getTotalWorldTime();
					if (tick > previousMessageTick + 20) {
						player.addChatMessage(new ChatComponentText(e.getMessage()));
						previousMessageTick = tick;
					}
					return true;
				}
			} else {
				player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("for.multiblock.error.notConnected")));
				return true;
			}
		}

		// Don't open the GUI if the multiblock isn't assembled
		if (controller == null || !controller.isAssembled()) {
			return false;
		}

		if (!world.isRemote) {
			part.openGui(player);
		}
		return true;
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		if (!Proxies.common.isSimulating(world)) {
			return;
		}

		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof MultiblockTileEntityBase) {
			MultiblockTileEntityBase part = (MultiblockTileEntityBase) tile;

			// drop inventory if we're the last part remaining
			if (part.getNeighboringParts().length == 0) {
				Utils.dropInventory(part, world, pos);
				if (tile instanceof ISocketable) {
					Utils.dropSockets((ISocketable) tile, tile.getWorld(), tile.getPos());
				}
			}
		}
		super.breakBlock(world, pos, state);
	}

}

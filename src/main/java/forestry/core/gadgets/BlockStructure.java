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

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import forestry.core.multiblock.MultiblockControllerBase;
import forestry.core.multiblock.MultiblockTileEntityBase;
import forestry.core.proxy.Proxies;
import forestry.core.utils.Utils;

public abstract class BlockStructure extends BlockForestry {

	public BlockStructure(Material material) {
		super(material);
		setHardness(1.0f);
	}

	@Override
	public boolean canSilkHarvest() {
		return false;
	}

	private long previousMessageTick = 0;

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {
		if (player.isSneaking()) {
			return false;
		}

		MultiblockTileEntityBase part;
		MultiblockControllerBase controller;

		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof MultiblockTileEntityBase) {
			part = (MultiblockTileEntityBase) tile;
			controller = part.getMultiblockController();
		} else {
			return false;
		}

		// If the player's hands are empty and they right-click on a multiblock, they get a
		// multiblock-debugging message if the machine is not assembled.
		if (player.getCurrentEquippedItem() == null) {
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
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {

		if (!Proxies.common.isSimulating(world)) {
			return;
		}

		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof MultiblockTileEntityBase) {
			MultiblockTileEntityBase part = (MultiblockTileEntityBase) tile;

			// drop inventory if we're the last part remaining
			if (part.getNeighboringParts().length == 0) {
				Utils.dropInventory(part, world, x, y, z);
			}
		}
		super.breakBlock(world, x, y, z, block, meta);
	}

}

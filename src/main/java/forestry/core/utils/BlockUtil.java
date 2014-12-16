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
package forestry.core.utils;

import buildcraft.api.core.Position;
import buildcraft.api.transport.IPipeConnection;
import buildcraft.api.transport.IPipeConnection.ConnectOverride;
import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.IPipeTile.PipeType;
import cofh.api.energy.IEnergyHandler;
import forestry.core.config.Defaults;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockLog;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockUtil {

	public static ArrayList<ItemStack> getBlockItemStack(World world, Vect posBlock) {
		Block block = world.getBlock(posBlock.x, posBlock.y, posBlock.z);
		int meta = world.getBlockMetadata(posBlock.x, posBlock.y, posBlock.z);

		return block.getDrops(world, posBlock.x, posBlock.y, posBlock.z, meta, 0);

	}

	/**
	 * Searches for inventories adjacent to block, excludes IPowerReceptor
	 * 
	 * @param world
	 * @param blockPos
	 * @param from
	 * @return
	 * @deprecated Use AdjacentInventoryCache instead
	 */
	@Deprecated
	public static IInventory[] getAdjacentInventories(World world, Vect blockPos, ForgeDirection from) {
		ArrayList<IInventory> inventories = new ArrayList<IInventory>();

		// WTF is this? A loop that only picks the opposite direction? -CovertJaguar
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			if (from != ForgeDirection.UNKNOWN && from != dir.getOpposite())
				continue;

			TileEntity entity = world.getTileEntity(blockPos.x + dir.offsetX, blockPos.y + dir.offsetY, blockPos.z + dir.offsetZ);
			if (entity != null)
				if (entity instanceof IInventory)
					if (!(entity instanceof TileEntityHopper))
						inventories.add((IInventory) entity);
		}

		return inventories.toArray(new IInventory[inventories.size()]);
	}

	/**
	 * Returns a list of adjacent pipes.
	 * 
	 * @param world
	 * @param blockPos
	 * @param from
	 * @return
	 */
	public static ForgeDirection[] getPipeDirections(World world, Vect blockPos, ForgeDirection from) {
		LinkedList<ForgeDirection> possiblePipes = new LinkedList<ForgeDirection>();

		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			if (from != ForgeDirection.UNKNOWN && from != dir.getOpposite())
				continue;

			Position posPipe = new Position(blockPos.x, blockPos.y, blockPos.z, dir);
			posPipe.moveForwards(1.0);

			TileEntity pipeEntry = world.getTileEntity((int) posPipe.x, (int) posPipe.y, (int) posPipe.z);

			if (pipeEntry instanceof IPipeTile) {
				IPipeTile pipe = (IPipeTile) pipeEntry;

				if (from != ForgeDirection.UNKNOWN && pipeEntry instanceof IPipeConnection) {
					if (((IPipeConnection) pipeEntry).overridePipeConnection(PipeType.ITEM, from) != ConnectOverride.DISCONNECT)
						possiblePipes.add(dir);
				} else if (pipe.getPipeType() == PipeType.ITEM && pipe.isPipeConnected(dir.getOpposite()))
					possiblePipes.add(dir);
			}
		}

		return possiblePipes.toArray(new ForgeDirection[possiblePipes.size()]);

	}

	public static ArrayList<ForgeDirection> filterPipeDirections(ForgeDirection[] allDirections, ForgeDirection[] exclude) {
		ArrayList<ForgeDirection> filtered = new ArrayList<ForgeDirection>();
		ArrayList<ForgeDirection> excludeList = new ArrayList<ForgeDirection>(Arrays.asList(exclude));

		for (ForgeDirection direction : allDirections) {
			if (!excludeList.contains(direction))
				filtered.add(direction);
		}

		return filtered;

	}

	public static boolean putFromStackIntoPipe(TileEntity tile, ArrayList<ForgeDirection> pipes, ItemStack stack) {

		if (stack == null)
			return false;
		if (stack.stackSize <= 0)
			return false;
		if (pipes.size() <= 0)
			return false;

		int choice = tile.getWorldObj().rand.nextInt(pipes.size());
		Position itemPos = new Position(tile.xCoord, tile.yCoord, tile.zCoord, pipes.get(choice));

		itemPos.x += 0.5;
		itemPos.y += 0.25;
		itemPos.z += 0.5;
		itemPos.moveForwards(0.5);

		Position pipePos = new Position(tile.xCoord, tile.yCoord, tile.zCoord, pipes.get(choice));
		pipePos.moveForwards(1.0);

		IPipeTile pipe = (IPipeTile) tile.getWorldObj().getTileEntity((int) pipePos.x, (int) pipePos.y, (int) pipePos.z);

		ItemStack payload = stack.splitStack(1);
		if (pipe.injectItem(payload, true, itemPos.orientation.getOpposite()) > 0)
			return true;
		else
			pipes.remove(choice);

		return false;
	}

	public static boolean isRFTile(ForgeDirection side, TileEntity tile) {
		if (tile == null)
			return false;

		if (!(tile instanceof IEnergyHandler))
			return false;

		IEnergyHandler receptor = (IEnergyHandler) tile;
		return receptor.canConnectEnergy(side);
	}

	public static boolean tryPlantPot(World world, int x, int y, int z, Block block) {

		int direction = getDirectionalMetadata(world, x, y, z);
		if (direction < 0)
			return false;

		world.setBlock(x, y, z, block, direction, Defaults.FLAG_BLOCK_SYNCH);
		return true;
	}

	public static int getDirectionalMetadata(World world, int x, int y, int z) {
		for (int i = 0; i < 4; i++) {
			if (!isValidPot(world, x, y, z, i))
				continue;
			return i;
		}
		return -1;
	}

	public static boolean isValidPot(World world, int x, int y, int z, int notchDirection) {
		x += Direction.offsetX[notchDirection];
		z += Direction.offsetZ[notchDirection];
		Block block = world.getBlock(x, y, z);
		if (block == Blocks.log)
			return BlockLog.func_150165_c(world.getBlockMetadata(x, y, z)) == 3;
		else
			return block.isWood(world, x, y, z);
	}

	public static int getMaturityPod(int metadata) {
		return BlockCocoa.func_149987_c(metadata);
	}
}

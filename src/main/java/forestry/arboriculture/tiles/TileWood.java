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
package forestry.arboriculture.tiles;

import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

import forestry.api.arboriculture.EnumWoodType;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.PluginArboriculture;
import forestry.arboriculture.blocks.BlockSlab;
import forestry.arboriculture.items.ItemBlockWood;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamable;
import forestry.core.network.packets.PacketTileStream;
import forestry.core.tiles.TileUtil;

public class TileWood extends TileEntity implements IStreamable {
	private EnumWoodType woodType;

	public TileWood() {
		this.woodType = EnumWoodType.LARCH;
	}

	public void setWoodType(EnumWoodType woodType) {
		this.woodType = woodType;
		markDirty();
	}

	public EnumWoodType getWoodType() {
		return woodType;
	}

	/* NETWORK */
	@Override
	public Packet getDescriptionPacket() {
		return new PacketTileStream(this).getPacket();
	}

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		data.writeVarInt(woodType.ordinal());
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		int ordinal = data.readVarInt();
		woodType = EnumWoodType.VALUES[ordinal];
		worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if (woodType != null) {
			nbt.setInteger("WT", woodType.ordinal());
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if (nbt.hasKey("WT")) {
			int ordinal = nbt.getInteger("WT");
			woodType = EnumWoodType.VALUES[ordinal];
		}
	}

	public static ItemStack getPickBlock(Block block, IBlockAccess world, BlockPos pos) {
		TileWood wood = getWoodTile(world, pos);
		if (wood == null) {
			return null;
		}
		EnumWoodType woodType = wood.getWoodType();

		int amount = 1;
		if (block instanceof BlockSlab) {
			BlockSlab blockSlab = (BlockSlab) block;
			if (blockSlab.isDouble()) {
				amount = 2;
				block = PluginArboriculture.blocks.slabs;
			}
		}

		ItemStack itemStack = new ItemStack(block, amount);
		ItemBlockWood.saveToItemStack(woodType, itemStack);
		return itemStack;
	}

	public static TileWood getWoodTile(IBlockAccess world, BlockPos pos) {
		return TileUtil.getTile(world, pos, TileWood.class);
	}

	public static <T extends Block & IWoodTyped> ArrayList<ItemStack> getDrops(T block, IBlockAccess world, BlockPos pos) {
		ArrayList<ItemStack> drops = new ArrayList<>();

		ItemStack stack = getPickBlock(block, world, pos);
		if (stack != null) {
			drops.add(stack);
		}

		return drops;
	}
}

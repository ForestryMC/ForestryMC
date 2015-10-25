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
package forestry.arboriculture.gadgets;

import java.io.IOException;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.WoodType;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketTileStream;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StackUtils;

public class TileWood extends TileEntity implements IStreamable {
	private WoodType woodType;

	public TileWood() {
		this.woodType = WoodType.ACACIA;
	}

	public void setWoodType(WoodType woodType) {
		this.woodType = woodType;
		markDirty();
	}

	public WoodType getWoodType() {
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
		woodType = WoodType.VALUES[ordinal];
		worldObj.markBlockRangeForRenderUpdate(pos, pos);
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
			woodType = WoodType.VALUES[ordinal];
		}
	}

	public static <T extends Block & IWoodTyped> boolean blockRemovedByPlayer(T block, World world, EntityPlayer player,
			BlockPos pos) {
		if (Proxies.common.isSimulating(world) && block.canHarvestBlock(world, pos, player)
				&& !player.capabilities.isCreativeMode) {

			TileEntity tile = world.getTileEntity(pos);
			;
			if (tile instanceof TileWood) {
				TileWood wood = (TileWood) tile;

				ItemStack stack = new ItemStack(block);
				NBTTagCompound compound = new NBTTagCompound();
				wood.getWoodType().saveToCompound(compound);
				stack.setTagCompound(compound);
				StackUtils.dropItemStackAsEntity(stack, world, pos.getX(), pos.getY(), pos.getZ());
			}
		}

		return world.setBlockToAir(pos);
	}

}

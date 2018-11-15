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
package forestry.lepidopterology.tiles;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.genetics.IAllele;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.IButterfly;
import forestry.api.lepidopterology.IButterflyCocoon;
import forestry.api.lepidopterology.IButterflyGenome;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.packets.PacketTileStream;
import forestry.core.owner.IOwnedTile;
import forestry.core.owner.IOwnerHandler;
import forestry.core.owner.OwnerHandler;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Log;
import forestry.core.utils.NBTUtilForestry;
import forestry.core.utils.NetworkUtil;
import forestry.lepidopterology.genetics.Butterfly;
import forestry.lepidopterology.genetics.ButterflyDefinition;

public class TileCocoon extends TileEntity implements IStreamable, IOwnedTile, IButterflyCocoon {
	private final OwnerHandler ownerHandler = new OwnerHandler();
	private int age;
	private int maturationTime;
	private IButterfly caterpillar = ButterflyDefinition.CabbageWhite.getIndividual();
	private boolean isSolid;

	public TileCocoon() {
	}

	public TileCocoon(boolean isSolid) {
		this.isSolid = isSolid;
		if (isSolid) {
			this.age = 2;
		}
	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		if (nbttagcompound.hasKey("Caterpillar")) {
			caterpillar = new Butterfly(nbttagcompound.getCompoundTag("Caterpillar"));
		}
		ownerHandler.readFromNBT(nbttagcompound);
		age = nbttagcompound.getInteger("Age");
		maturationTime = nbttagcompound.getInteger("CATMAT");
		isSolid = nbttagcompound.getBoolean("isSolid");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound = super.writeToNBT(nbttagcompound);

		NBTTagCompound subcompound = new NBTTagCompound();
		caterpillar.writeToNBT(subcompound);
		nbttagcompound.setTag("Caterpillar", subcompound);

		ownerHandler.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("Age", age);
		nbttagcompound.setInteger("CATMAT", maturationTime);
		nbttagcompound.setBoolean("isSolid", isSolid);
		return nbttagcompound;
	}

	@Override
	public void writeData(PacketBufferForestry data) {
		IButterfly caterpillar = getCaterpillar();
		String speciesUID = caterpillar.getIdent();
		data.writeString(speciesUID);
		data.writeInt(age);
	}

	@Override
	public void readData(PacketBufferForestry data) {
		String speciesUID = data.readString();
		IButterfly caterpillar = getButterfly(speciesUID);
		setCaterpillar(caterpillar);
		age = data.readInt();
	}

	private static IButterfly getButterfly(String speciesUID) {
		IAllele[] butterflyTemplate = ButterflyManager.butterflyRoot.getTemplate(speciesUID);
		Preconditions.checkNotNull(butterflyTemplate, "Could not find butterfly template for species: %s", speciesUID);
		return ButterflyManager.butterflyRoot.templateAsIndividual(butterflyTemplate);
	}

	@Override
	public IOwnerHandler getOwnerHandler() {
		return ownerHandler;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return !Block.isEqualTo(oldState.getBlock(), newSate.getBlock());
	}

	/* INETWORKEDENTITY */
	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.getPos(), 0, getUpdateTag());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound tag = super.getUpdateTag();
		return NBTUtilForestry.writeStreamableToNbt(this, tag);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleUpdateTag(NBTTagCompound tag) {
		int oldAge = age;
		super.handleUpdateTag(tag);
		NBTUtilForestry.readStreamableFromNbt(this, tag);
		if (oldAge != age) {
			world.markBlockRangeForRenderUpdate(pos, pos);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		NBTTagCompound nbt = pkt.getNbtCompound();
		handleUpdateTag(nbt);
	}

	public void onBlockTick() {
		maturationTime++;

		IButterflyGenome caterpillarGenome = caterpillar.getGenome();
		int caterpillarMatureTime = Math
			.round((float) caterpillarGenome.getLifespan() / (caterpillarGenome.getFertility() * 2));

		if (maturationTime >= caterpillarMatureTime) {
			if (age < 2) {
				age++;
				maturationTime = 0;
				IBlockState blockState = world.getBlockState(pos);
				world.notifyBlockUpdate(pos, blockState, blockState, 0);
			} else if (caterpillar.canTakeFlight(world, getPos().getX(), getPos().getY(), getPos().getZ())) {
				NonNullList<ItemStack> cocoonDrops = caterpillar.getCocoonDrop(this);
				for (ItemStack drop : cocoonDrops) {
					ItemStackUtil.dropItemStackAsEntity(drop, world, pos);
				}
				world.setBlockToAir(getPos());
				attemptButterflySpawn(world, caterpillar, getPos());
			}
		}
	}

	private boolean isListEmpty(NonNullList<ItemStack> cocoonDrops) {
		if (cocoonDrops.isEmpty()) {
			return true;
		}
		for (ItemStack stack : cocoonDrops) {
			if (!stack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	private static void attemptButterflySpawn(World world, IButterfly butterfly, BlockPos pos) {
		EntityLiving entityLiving = ButterflyManager.butterflyRoot.spawnButterflyInWorld(world, butterfly.copy(),
			pos.getX(), pos.getY() + 0.1f, pos.getZ());
		Log.trace("A caterpillar '%s' hatched at %s/%s/%s.", butterfly.getDisplayName(), pos.getX(), pos.getY(),
			pos.getZ());
	}

	@Override
	public BlockPos getCoordinates() {
		return getPos();
	}

	@Override
	public IButterfly getCaterpillar() {
		return caterpillar;
	}

	@Override
	public void setCaterpillar(IButterfly butterfly) {
		this.caterpillar = butterfly;
		sendNetworkUpdate();
	}

	private void sendNetworkUpdate() {
		NetworkUtil.sendNetworkPacket(new PacketTileStream(this), pos, world);
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public NonNullList<ItemStack> getCocoonDrops() {
		return caterpillar.getCocoonDrop(this);
	}

	@Override
	public boolean isSolid() {
		return isSolid;
	}

}

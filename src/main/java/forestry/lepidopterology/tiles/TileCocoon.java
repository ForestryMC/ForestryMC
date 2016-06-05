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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.api.genetics.IAllele;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.IButterfly;
import forestry.api.lepidopterology.IButterflyCocoon;
import forestry.api.lepidopterology.IButterflyGenome;
import forestry.api.lepidopterology.IButterflyNursery;
import forestry.api.multiblock.IGreenhouseComponent;
import forestry.core.access.IOwnable;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamable;
import forestry.core.network.packets.PacketTileStream;
import forestry.core.proxy.Proxies;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Log;
import forestry.core.utils.NBTUtilForestry;
import forestry.core.utils.PlayerUtil;
import forestry.greenhouse.multiblock.GreenhouseController;
import forestry.lepidopterology.genetics.Butterfly;
import forestry.lepidopterology.genetics.ButterflyDefinition;

public class TileCocoon extends TileEntity implements IStreamable, IOwnable, IButterflyCocoon {
	private int age;
	private int maturationTime;
	private IButterfly caterpillar = ButterflyDefinition.CabbageWhite.getIndividual();
	private GameProfile owner;
	private BlockPos nursery;
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
		if (nbttagcompound.hasKey("owner")) {
			owner = PlayerUtil.readGameProfileFromNBT(nbttagcompound.getCompoundTag("owner"));
		}
		if (nbttagcompound.hasKey("nursery")) {
			NBTTagCompound nbt = nbttagcompound.getCompoundTag("nursery");
			int x = nbt.getInteger("x");
			int y = nbt.getInteger("y");
			int z = nbt.getInteger("z");
			nursery = new BlockPos(x, y, z);
		}
		age = nbttagcompound.getInteger("Age");
		maturationTime = nbttagcompound.getInteger("CATMAT");
		isSolid = nbttagcompound.getBoolean("isSolid");
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound = super.writeToNBT(nbttagcompound);

		if (caterpillar != null) {
			NBTTagCompound subcompound = new NBTTagCompound();
			caterpillar.writeToNBT(subcompound);
			nbttagcompound.setTag("Caterpillar", subcompound);
		}
		if (this.owner != null) {
			NBTTagCompound nbt = new NBTTagCompound();
			PlayerUtil.writeGameProfile(nbt, owner);
			nbttagcompound.setTag("owner", nbt);
		}
		if (nursery != null) {
			BlockPos pos = nursery;
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("x", pos.getX());
			nbt.setInteger("y", pos.getY());
			nbt.setInteger("z", pos.getZ());
			nbttagcompound.setTag("nursery", nbt);
		}
		nbttagcompound.setInteger("Age", age);
		nbttagcompound.setInteger("CATMAT", maturationTime);
		nbttagcompound.setBoolean("isSolid", isSolid);
		return nbttagcompound;
	}

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		String speciesUID = "";
		IButterfly caterpillar = getCaterpillar();
		if (caterpillar != null) {
			speciesUID = caterpillar.getIdent();
		}
		data.writeUTF(speciesUID);
		data.writeInt(age);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		String speciesUID = data.readUTF();
		IButterfly caterpillar = getButterfly(speciesUID);
		setCaterpillar(caterpillar);
		age = data.readInt();
	}

	private static IButterfly getButterfly(String speciesUID) {
		IAllele[] butterflyTemplate = ButterflyManager.butterflyRoot.getTemplate(speciesUID);
		if (butterflyTemplate == null) {
			return null;
		}
		return ButterflyManager.butterflyRoot.templateAsIndividual(butterflyTemplate);
	}

	/* IOwnable */
	@Override
	public GameProfile getOwner() {
		return owner;
	}

	@Override
	public void setOwner(GameProfile owner) {
		this.owner = owner;
	}

	@Override
	public boolean isOwned() {
		return owner != null;
	}

	@Override
	public boolean isOwner(EntityPlayer player) {
		return player != null && PlayerUtil.isSameGameProfile(player.getGameProfile(), owner);
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

	@Nonnull
	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound tag = super.getUpdateTag();
		return NBTUtilForestry.writeStreamableToNbt(this, tag);
	}

	@Override
	public void handleUpdateTag(@Nonnull NBTTagCompound tag) {
		super.handleUpdateTag(tag);
		NBTUtilForestry.readStreamableFromNbt(this, tag);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		NBTTagCompound nbt = pkt.getNbtCompound();
		handleUpdateTag(nbt);
	}

	public void onBlockTick() {
		if (caterpillar == null) {
			worldObj.setBlockToAir(getPos());
			return;
		}

		maturationTime++;

		IButterflyGenome caterpillarGenome = caterpillar.getGenome();
		int caterpillarMatureTime = Math.round((float) caterpillarGenome.getLifespan() / (caterpillarGenome.getFertility() * 2));

		if (maturationTime >= caterpillarMatureTime) {
			if (age < 2) {
				age++;
				maturationTime = 0;
			} else if (caterpillar.canTakeFlight(worldObj, getPos().getX(), getPos().getY(), getPos().getZ())) {
				IGreenhouseComponent.ButterflyHatch hatch = GreenhouseController.getGreenhouseButterflyHatch(worldObj, pos);
				ItemStack[] cocoonDrops;
				if (hatch != null) {
					cocoonDrops = hatch.addCocoonLoot(this);
				} else {
					cocoonDrops = caterpillar.getCocoonDrop(this);
				}
				for (ItemStack drop : cocoonDrops) {
					ItemStackUtil.dropItemStackAsEntity(drop, worldObj, pos);
				}
				worldObj.setBlockToAir(getPos());
				attemptButterflySpawn(worldObj, caterpillar, getPos());
			}
		}
	}
	
	private static void attemptButterflySpawn(World world, IButterfly butterfly, BlockPos pos) {
		if (ButterflyManager.butterflyRoot.spawnButterflyInWorld(world, butterfly.copy(), pos.getX(), pos.getY() + 0.1f, pos.getZ()) != null) {
			Log.trace("A caterpillar '%s' hatched at %s/%s/%s.", butterfly.getDisplayName(), pos.getX(), pos.getY(), pos.getZ());
		}
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
	public IButterflyNursery getNursery() {
		TileEntity nursery = worldObj.getTileEntity(this.nursery);
		if (nursery instanceof IButterflyNursery) {
			return (IButterflyNursery) nursery;
		}
		return null;
	}
	
	@Override
	public void setNursery(IButterflyNursery nursery) {
		this.nursery = nursery.getCoordinates();
	}

	@Override
	public void setCaterpillar(IButterfly butterfly) {
		this.caterpillar = butterfly;
		sendNetworkUpdate();
	}

	private void sendNetworkUpdate() {
		Proxies.net.sendNetworkPacket(new PacketTileStream(this), worldObj);
	}
	
	public int getAge() {
		return age;
	}
	
	public void setAge(int age) {
		this.age = age;
	}
	
	public ItemStack[] getCocoonDrops() {
		return caterpillar.getCocoonDrop(this);
	}

	@Override
	public boolean isSolid() {
		return isSolid;
	}

}

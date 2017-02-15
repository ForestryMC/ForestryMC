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

import javax.annotation.Nullable;
import java.io.IOException;

import com.google.common.base.Preconditions;
import forestry.api.genetics.IAllele;
import forestry.api.greenhouse.GreenhouseManager;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.IButterfly;
import forestry.api.lepidopterology.IButterflyCocoon;
import forestry.api.lepidopterology.IButterflyGenome;
import forestry.api.lepidopterology.IButterflyNursery;
import forestry.api.multiblock.IGreenhouseComponent;
import forestry.api.multiblock.IGreenhouseComponent.ButterflyHatch;
import forestry.api.multiblock.IGreenhouseController;
import forestry.api.multiblock.IMultiblockComponent;
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
import forestry.greenhouse.multiblock.IGreenhouseControllerInternal;
import forestry.lepidopterology.genetics.Butterfly;
import forestry.lepidopterology.genetics.ButterflyDefinition;
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

public class TileCocoon extends TileEntity implements IStreamable, IOwnedTile, IButterflyCocoon {
	private final OwnerHandler ownerHandler = new OwnerHandler();
	private int age;
	private int maturationTime;
	private IButterfly caterpillar = ButterflyDefinition.CabbageWhite.getIndividual();
	@Nullable
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
		ownerHandler.readFromNBT(nbttagcompound);
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

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound = super.writeToNBT(nbttagcompound);

		NBTTagCompound subcompound = new NBTTagCompound();
		caterpillar.writeToNBT(subcompound);
		nbttagcompound.setTag("Caterpillar", subcompound);

		ownerHandler.writeToNBT(nbttagcompound);

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
	public void writeData(PacketBufferForestry data) {
		IButterfly caterpillar = getCaterpillar();
		String speciesUID = caterpillar.getIdent();
		data.writeString(speciesUID);
		data.writeInt(age);
	}

	@Override
	public void readData(PacketBufferForestry data) throws IOException {
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
				IGreenhouseComponent.ButterflyHatch hatch = getButterflyHatch(world, pos);
				NonNullList<ItemStack> cocoonDrops;
				if (hatch != null) {
					cocoonDrops = hatch.addCocoonLoot(this);
				} else {
					cocoonDrops = caterpillar.getCocoonDrop(this);
				}
				for (ItemStack drop : cocoonDrops) {
					ItemStackUtil.dropItemStackAsEntity(drop, world, pos);
				}
				world.setBlockToAir(getPos());
				attemptButterflySpawn(world, caterpillar, getPos());
			}
		}
	}

	@Nullable
	public ButterflyHatch getButterflyHatch(World world, BlockPos pos) {
		if (GreenhouseManager.greenhouseHelper == null
				|| GreenhouseManager.greenhouseHelper.getGreenhouseController(world, pos) == null) {
			return null;
		}
		IGreenhouseController controller = GreenhouseManager.greenhouseHelper.getGreenhouseController(world, pos);
		if (controller != null) {
			if (controller instanceof IGreenhouseControllerInternal) {
				return ((IGreenhouseControllerInternal) controller).getButterflyHatch();
			}

			for (IMultiblockComponent greenhouse : controller.getComponents()) {
				if (greenhouse instanceof ButterflyHatch) {
					return (ButterflyHatch) greenhouse;
				}
			}
		}
		return null;
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
	@Nullable
	public IButterflyNursery getNursery() {
		if (this.nursery == null) {
			return null;
		}
		TileEntity nursery = world.getTileEntity(this.nursery);
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

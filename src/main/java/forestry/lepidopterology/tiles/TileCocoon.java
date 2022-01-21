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

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.IButterflyCocoon;
import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.api.lepidopterology.genetics.IButterfly;
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
import forestry.lepidopterology.features.LepidopterologyTiles;
import forestry.lepidopterology.genetics.Butterfly;
import forestry.lepidopterology.genetics.ButterflyDefinition;

import genetics.api.alleles.IAllele;
import genetics.api.individual.IGenome;

public class TileCocoon extends BlockEntity implements IStreamable, IOwnedTile, IButterflyCocoon {
	private final OwnerHandler ownerHandler = new OwnerHandler();
	private int age;
	private int maturationTime;
	private IButterfly caterpillar = ButterflyDefinition.CabbageWhite.createIndividual();
	private boolean isSolid;

	public TileCocoon(BlockPos pos, BlockState state) {
		super(LepidopterologyTiles.COCOON.tileType(), pos, state);
	}

	public TileCocoon(BlockPos pos, BlockState state, boolean isSolid) {
		super(isSolid ? LepidopterologyTiles.SOLID_COCOON.tileType() : LepidopterologyTiles.COCOON.tileType(), pos, state);
		this.isSolid = isSolid;
		if (isSolid) {
			this.age = 2;
		}
	}

	/* SAVING & LOADING */
	@Override
	public void load(BlockState state, CompoundTag compoundNBT) {
		super.load(state, compoundNBT);

		if (compoundNBT.contains("Caterpillar")) {
			caterpillar = new Butterfly(compoundNBT.getCompound("Caterpillar"));
		}
		ownerHandler.read(compoundNBT);
		age = compoundNBT.getInt("Age");
		maturationTime = compoundNBT.getInt("CATMAT");
		isSolid = compoundNBT.getBoolean("isSolid");
	}

	@Override
	public CompoundTag save(CompoundTag compoundNBT) {
		compoundNBT = super.save(compoundNBT);

		CompoundTag subcompound = new CompoundTag();
		caterpillar.write(subcompound);
		compoundNBT.put("Caterpillar", subcompound);

		ownerHandler.write(compoundNBT);

		compoundNBT.putInt("Age", age);
		compoundNBT.putInt("CATMAT", maturationTime);
		compoundNBT.putBoolean("isSolid", isSolid);
		return compoundNBT;
	}

	@Override
	public void writeData(PacketBufferForestry data) {
		IButterfly caterpillar = getCaterpillar();
		String speciesUID = caterpillar.getIdentifier();
		data.writeUtf(speciesUID);
		data.writeInt(age);
	}

	@Override
	public void readData(PacketBufferForestry data) {
		String speciesUID = data.readUtf();
		IButterfly caterpillar = getButterfly(speciesUID);
		setCaterpillar(caterpillar);
		age = data.readInt();
	}

	private static IButterfly getButterfly(String speciesUID) {
		IAllele[] butterflyTemplate = ButterflyManager.butterflyRoot.getTemplates().getTemplate(speciesUID);
		Preconditions.checkNotNull(butterflyTemplate, "Could not find butterfly template for species: %s", speciesUID);
		return ButterflyManager.butterflyRoot.templateAsIndividual(butterflyTemplate);
	}

	@Override
	public IOwnerHandler getOwnerHandler() {
		return ownerHandler;
	}

	//TODO moved to block.onReplaced
	//	@Override
	//	public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newSate) {
	//		return !Block.isEqualTo(oldState.getBlock(), newSate.getBlock());
	//	}

	/* INETWORKEDENTITY */
	@Nullable
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return new ClientboundBlockEntityDataPacket(this.getBlockPos(), 0, getUpdateTag());
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = super.getUpdateTag();
		return NBTUtilForestry.writeStreamableToNbt(this, tag);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void handleUpdateTag(BlockState state, CompoundTag tag) {
		int oldAge = age;
		super.handleUpdateTag(state, tag);
		NBTUtilForestry.readStreamableFromNbt(this, tag);
		if (oldAge != age) {
			Minecraft.getInstance().levelRenderer.setSectionDirty(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
			//			world.markBlockRangeForRenderUpdate(pos, pos);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		super.onDataPacket(net, pkt);
		CompoundTag nbt = pkt.getTag();
		handleUpdateTag(getBlockState(), nbt);
	}

	public void onBlockTick() {
		maturationTime++;

		IGenome caterpillarGenome = caterpillar.getGenome();
		int caterpillarMatureTime = Math
				.round((float) caterpillarGenome.getActiveValue(ButterflyChromosomes.LIFESPAN) / (caterpillarGenome.getActiveValue(ButterflyChromosomes.FERTILITY) * 2));

		if (maturationTime >= caterpillarMatureTime) {
			if (age < 2) {
				age++;
				maturationTime = 0;
				BlockState blockState = level.getBlockState(worldPosition);
				level.sendBlockUpdated(worldPosition, blockState, blockState, 0);
			} else if (caterpillar.canTakeFlight(level, getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ())) {
				NonNullList<ItemStack> cocoonDrops = caterpillar.getCocoonDrop(this);
				for (ItemStack drop : cocoonDrops) {
					ItemStackUtil.dropItemStackAsEntity(drop, level, worldPosition);
				}
				level.setBlockAndUpdate(getBlockPos(), Blocks.AIR.defaultBlockState());
				attemptButterflySpawn(level, caterpillar, getBlockPos());
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

	private static void attemptButterflySpawn(Level world, IButterfly butterfly, BlockPos pos) {
		Mob entityLiving = ButterflyManager.butterflyRoot.spawnButterflyInWorld(world, butterfly.copy(),
				pos.getX(), pos.getY() + 0.1f, pos.getZ());
		Log.trace("A caterpillar '%s' hatched at %s/%s/%s.", butterfly.getDisplayName(), pos.getX(), pos.getY(),
				pos.getZ());
	}

	@Override
	public BlockPos getCoordinates() {
		return getBlockPos();
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
		NetworkUtil.sendNetworkPacket(new PacketTileStream(this), worldPosition, level);
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

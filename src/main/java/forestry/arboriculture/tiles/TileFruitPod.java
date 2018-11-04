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

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.BlockCocoa;
import net.minecraft.block.state.IBlockState;
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

import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IFruitBearer;
import forestry.api.genetics.IFruitFamily;
import forestry.core.config.Constants;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketBufferForestry;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.NBTUtilForestry;

public class TileFruitPod extends TileEntity implements IFruitBearer, IStreamable {

	private static final short MAX_MATURITY = 2;
	private static final ITreeGenome defaultGenome = TreeManager.treeRoot.templateAsGenome(TreeManager.treeRoot.getDefaultTemplate());
	private static final IAlleleFruit defaultAllele = (IAlleleFruit) AlleleManager.alleleRegistry.getAllele(Constants.MOD_ID + ".fruitCocoa");

	private ITreeGenome genome = defaultGenome;
	private IAlleleFruit allele = defaultAllele;
	private short maturity;
	private float yield;

	public TileFruitPod() {

	}

	public void setProperties(ITreeGenome genome, IAlleleFruit allele, float yield) {
		this.genome = genome;
		this.allele = allele;
		this.yield = yield;
		markDirty();
	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		IAllele stored = AlleleManager.alleleRegistry.getAllele(nbttagcompound.getString("UID"));
		if (stored instanceof IAlleleFruit) {
			allele = (IAlleleFruit) stored;
		} else {
			allele = defaultAllele;
		}

		maturity = nbttagcompound.getShort("MT");
		yield = nbttagcompound.getFloat("SP");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound = super.writeToNBT(nbttagcompound);
		nbttagcompound.setString("UID", allele.getUID());
		nbttagcompound.setShort("MT", maturity);
		nbttagcompound.setFloat("SP", yield);
		return nbttagcompound;
	}

	/* UPDATING */
	public void onBlockTick(World world, BlockPos pos, IBlockState state, Random rand) {
		if (canMature() && rand.nextFloat() <= yield) {
			addRipeness(0.5f);
		}
	}

	public boolean canMature() {
		return maturity < MAX_MATURITY;
	}

	public short getMaturity() {
		return maturity;
	}

	public ItemStack getPickBlock() {
		Map<ItemStack, Float> products = allele.getProvider().getProducts();

		ItemStack pickBlock = ItemStack.EMPTY;
		Float maxChance = 0.0f;
		for (Map.Entry<ItemStack, Float> product : products.entrySet()) {
			if (maxChance < product.getValue()) {
				maxChance = product.getValue();
				pickBlock = product.getKey().copy();
			}
		}

		pickBlock.setCount(1);
		return pickBlock;
	}

	public NonNullList<ItemStack> getDrops() {
		return allele.getProvider().getFruits(genome, world, getPos(), maturity);
	}

	/* NETWORK */
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
		super.handleUpdateTag(tag);
		NBTUtilForestry.readStreamableFromNbt(this, tag);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		NBTTagCompound nbt = pkt.getNbtCompound();
		handleUpdateTag(nbt);
	}

	/* IFRUITBEARER */
	@Override
	public boolean hasFruit() {
		return true;
	}

	@Override
	public IFruitFamily getFruitFamily() {
		return allele.getProvider().getFamily();
	}

	@Override
	public NonNullList<ItemStack> pickFruit(ItemStack tool) {
		NonNullList<ItemStack> fruits = getDrops();
		maturity = 0;

		IBlockState oldState = world.getBlockState(getPos());
		IBlockState newState = oldState.withProperty(BlockCocoa.AGE, 0);
		BlockUtil.setBlockWithBreakSound(world, getPos(), newState, oldState);

		return fruits;
	}

	@Override
	public float getRipeness() {
		return (float) maturity / MAX_MATURITY;
	}

	@Override
	public void addRipeness(float add) {
		int previousAge = (int) Math.floor(maturity);

		maturity += MAX_MATURITY * add;
		if (maturity > MAX_MATURITY) {
			maturity = MAX_MATURITY;
		}

		int age = (int) Math.floor(maturity);
		if (age - previousAge > 0) {
			IBlockState state = world.getBlockState(getPos()).withProperty(BlockCocoa.AGE, age);
			world.setBlockState(getPos(), state);
		}
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void writeData(PacketBufferForestry data) {
		if (allele != defaultAllele) {
			data.writeString(allele.getUID());
		} else {
			data.writeString("");
		}
	}

	@Override
	public void readData(PacketBufferForestry data) {
		IAllele stored = AlleleManager.alleleRegistry.getAllele(data.readString());
		if (stored instanceof IAlleleFruit) {
			allele = (IAlleleFruit) stored;
		} else {
			allele = defaultAllele;
		}
		world.markBlockRangeForRenderUpdate(getPos(), getPos());
	}
}

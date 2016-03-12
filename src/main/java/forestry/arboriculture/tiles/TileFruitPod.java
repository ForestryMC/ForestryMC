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
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.minecraft.block.BlockCocoa;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IFruitBearer;
import forestry.api.genetics.IFruitFamily;
import forestry.core.config.Constants;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamable;
import forestry.core.network.packets.PacketTileStream;
import forestry.core.utils.Log;

public class TileFruitPod extends TileEntity implements IFruitBearer, IStreamable {

	private static final short MAX_MATURITY = 2;

	private IAlleleFruit allele;

	private short maturity;
	private float sappiness;

	public TileFruitPod() {
		Log.debug("Made a fruit pod");
	}

	public void setProperties(IAlleleFruit allele, float sappiness) {
		this.allele = allele;
		this.sappiness = sappiness;
	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		IAllele stored = AlleleManager.alleleRegistry.getAllele(nbttagcompound.getString("UID"));
		if (stored instanceof IAlleleFruit) {
			allele = (IAlleleFruit) stored;
		} else {
			allele = (IAlleleFruit) AlleleManager.alleleRegistry.getAllele(Constants.MOD_ID + ".fruitCocoa");
		}

		maturity = nbttagcompound.getShort("MT");
		sappiness = nbttagcompound.getFloat("SP");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		if (allele != null) {
			nbttagcompound.setString("UID", allele.getUID());
		}

		nbttagcompound.setShort("MT", maturity);
		nbttagcompound.setFloat("SP", sappiness);
	}

	/* UPDATING */
	public void onBlockTick() {
		if (canMature() && worldObj.rand.nextFloat() <= sappiness) {
			addRipeness(0.5f);
		}
	}

	public boolean canMature() {
		return maturity < MAX_MATURITY;
	}

	public short getMaturity() {
		return maturity;
	}

	@Nullable
	public ItemStack getPickBlock() {
		Map<ItemStack, Float> products = allele.getProvider().getProducts();

		ItemStack pickBlock = null;
		Float maxChance = 0.0f;
		for (Map.Entry<ItemStack, Float> product : products.entrySet()) {
			if (maxChance < product.getValue()) {
				maxChance = product.getValue();
				pickBlock = product.getKey().copy();
			}
		}

		pickBlock.stackSize = 1;
		return pickBlock;
	}

	public List<ItemStack> getDrops() {
		return allele.getProvider().getFruits(null, worldObj, getPos(), maturity);
	}

	/* NETWORK */
	@Override
	public Packet getDescriptionPacket() {
		return new PacketTileStream(this).getPacket();
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
	public Collection<ItemStack> pickFruit(ItemStack tool) {
		Collection<ItemStack> fruits = getDrops();
		maturity = 0;

		IBlockState state = worldObj.getBlockState(getPos()).withProperty(BlockCocoa.AGE, 0);
		worldObj.setBlockState(getPos(), state);

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
			IBlockState state = worldObj.getBlockState(getPos()).withProperty(BlockCocoa.AGE, age);
			worldObj.setBlockState(getPos(), state);
		}
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		if (allele != null) {
			data.writeUTF(allele.getUID());
		} else {
			data.writeUTF("");
		}
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		IAllele stored = AlleleManager.alleleRegistry.getAllele(data.readUTF());
		if (stored instanceof IAlleleFruit) {
			allele = (IAlleleFruit) stored;
		} else {
			allele = (IAlleleFruit) AlleleManager.alleleRegistry.getAllele(Constants.MOD_ID + ".fruitCocoa");
		}
		worldObj.markBlockForUpdate(getPos());
	}
}

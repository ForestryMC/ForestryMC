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
import java.util.Optional;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.CocoaBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import genetics.api.GeneticsAPI;
import genetics.api.alleles.IAllele;
import genetics.api.individual.IGenome;

import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.genetics.IAlleleFruit;
import forestry.api.genetics.IFruitBearer;
import forestry.api.genetics.IFruitFamily;
import forestry.arboriculture.ModuleArboriculture;
import forestry.core.config.Constants;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketBufferForestry;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.NBTUtilForestry;
import forestry.core.utils.RenderUtil;

public class TileFruitPod extends TileEntity implements IFruitBearer, IStreamable {

	private static final short MAX_MATURITY = 2;
	private static final IGenome defaultGenome = TreeManager.treeRoot.getKaryotype().getDefaultGenome();
	private static final IAlleleFruit defaultAllele = (IAlleleFruit) GeneticsAPI.apiInstance.getAlleleRegistry().getAllele(Constants.MOD_ID + ".fruitCocoa").get();

	private IGenome genome = defaultGenome;
	private IAlleleFruit allele = defaultAllele;
	private short maturity;
	private float yield;

	public TileFruitPod() {
		super(ModuleArboriculture.getTiles().pods);
	}

	public void setProperties(IGenome genome, IAlleleFruit allele, float yield) {
		this.genome = genome;
		this.allele = allele;
		this.yield = yield;
		markDirty();
	}

	/* SAVING & LOADING */
	@Override
	public void read(CompoundNBT compoundNBT) {
		super.read(compoundNBT);

		Optional<IAllele> optionalAllele = GeneticsAPI.apiInstance.getAlleleRegistry().getAllele(compoundNBT.getString("UID"));
		if (!optionalAllele.isPresent()) {
			allele = defaultAllele;
		} else {
			IAllele stored = optionalAllele.get();
			if (stored instanceof IAlleleFruit) {
				allele = (IAlleleFruit) stored;
			} else {
				allele = defaultAllele;
			}
		}

		maturity = compoundNBT.getShort("MT");
		yield = compoundNBT.getFloat("SP");
	}

	@Override
	public CompoundNBT write(CompoundNBT compoundNBT) {
		compoundNBT = super.write(compoundNBT);
		compoundNBT.putString("UID", allele.getRegistryName().toString());
		compoundNBT.putShort("MT", maturity);
		compoundNBT.putFloat("SP", yield);
		return compoundNBT;
	}

	/* UPDATING */
	public void onBlockTick(World world, BlockPos pos, BlockState state, Random rand) {
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
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.getPos(), 0, getUpdateTag());
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT tag = super.getUpdateTag();
		return NBTUtilForestry.writeStreamableToNbt(this, tag);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void handleUpdateTag(CompoundNBT tag) {
		super.handleUpdateTag(tag);
		NBTUtilForestry.readStreamableFromNbt(this, tag);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		super.onDataPacket(net, pkt);
		CompoundNBT nbt = pkt.getNbtCompound();
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

		BlockState oldState = world.getBlockState(getPos());
		BlockState newState = oldState.with(CocoaBlock.AGE, 0);
		BlockUtil.setBlockWithBreakSound(world, getPos(), newState, oldState);

		return fruits;
	}

	@Override
	public float getRipeness() {
		return (float) maturity / MAX_MATURITY;
	}

	@Override
	public void addRipeness(float add) {
		int previousAge = maturity;

		maturity += MAX_MATURITY * add;
		if (maturity > MAX_MATURITY) {
			maturity = MAX_MATURITY;
		}

		int age = maturity;
		if (age - previousAge > 0) {
			BlockState state = world.getBlockState(getPos()).with(CocoaBlock.AGE, age);
			world.setBlockState(getPos(), state);
		}
	}

	@Override
	public void writeData(PacketBufferForestry data) {
		if (allele != defaultAllele) {
			data.writeString(allele.getRegistryName().toString());
		} else {
			data.writeString("");
		}
	}

	@Override
	public void readData(PacketBufferForestry data) {
		Optional<IAllele> optionalAllele = GeneticsAPI.apiInstance.getAlleleRegistry().getAllele(data.readString());
		if (!optionalAllele.isPresent()) {
			allele = defaultAllele;
		} else {
			IAllele stored = optionalAllele.get();
			if (stored instanceof IAlleleFruit) {
				allele = (IAlleleFruit) stored;
			} else {
				allele = defaultAllele;
			}
		}
		RenderUtil.markForUpdate(getPos());
	}
}

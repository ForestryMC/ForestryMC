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
import java.util.Arrays;
import java.util.Collection;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IFruitBearer;
import forestry.api.genetics.IFruitFamily;
import forestry.arboriculture.network.IRipeningPacketReceiver;
import forestry.arboriculture.network.packets.PacketRipeningUpdate;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamable;
import forestry.core.network.packets.PacketTileStream;
import forestry.core.proxy.Proxies;
import forestry.core.render.TextureManager;

public class TileFruitPod extends TileEntity implements IFruitBearer, IStreamable, IRipeningPacketReceiver {

	private static final short MAX_MATURITY = 2;

	private IAlleleFruit allele;

	private short maturity;
	private int[] indices = new int[0];
	private float sappiness;

	public void setFruit(IAlleleFruit allele, float sappiness, short[] indices) {
		this.allele = allele;
		this.sappiness = sappiness;
		this.indices = new int[indices.length];
		for (int i = 0; i < indices.length; i++) {
			this.indices[i] = indices[i];
		}
	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		IAllele stored = AlleleManager.alleleRegistry.getAllele(nbttagcompound.getString("UID"));
		if (stored != null && stored instanceof IAlleleFruit) {
			allele = (IAlleleFruit) stored;
		} else {
			allele = (IAlleleFruit) AlleleManager.alleleRegistry.getAllele("fruitCocoa");
		}

		maturity = nbttagcompound.getShort("MT");
		sappiness = nbttagcompound.getFloat("SP");
		indices = nbttagcompound.getIntArray("IN");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		if (allele != null) {
			nbttagcompound.setString("UID", allele.getUID());
		}

		nbttagcompound.setShort("MT", maturity);
		nbttagcompound.setFloat("SP", sappiness);
		nbttagcompound.setIntArray("IN", indices);
	}

	/* UPDATING */

	/**
	 * This doesn't use normal TE updates
	 */
	@Override
	public boolean canUpdate() {
		return false;
	}

	public void onBlockTick() {
		if (canMature() && worldObj.rand.nextFloat() <= sappiness) {
			mature();
		}
	}

	public void mature() {
		maturity++;
		sendNetworkUpdateRipening();
	}

	public boolean canMature() {
		return maturity < MAX_MATURITY;
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIcon() {
		if (maturity < indices.length) {
			return TextureManager.getInstance().getIcon((short) indices[maturity]);
		} else {
			return null;
		}
	}

	public short getMaturity() {
		return maturity;
	}

	public ItemStack[] getDrop() {
		return allele.getProvider().getFruits(null, worldObj, xCoord, yCoord, zCoord, maturity);
	}

	/* NETWORK */
	@Override
	public Packet getDescriptionPacket() {
		return new PacketTileStream(this).getPacket();
	}

	private void sendNetworkUpdateRipening() {
		Proxies.net.sendNetworkPacket(new PacketRipeningUpdate(this), worldObj);
	}

	@Override
	public void fromRipeningPacket(int newMaturity) {
		if (newMaturity == maturity) {
			return;
		}
		maturity = (short) newMaturity;
		worldObj.func_147479_m(xCoord, yCoord, zCoord);
	}

	/* IFRUITBEARER */
	@Override
	public boolean hasFruit() {
		return true;
	}

	@Override
	public IFruitFamily getFruitFamily() {
		if (allele == null) {
			return null;
		}
		return allele.getProvider().getFamily();
	}

	@Override
	public Collection<ItemStack> pickFruit(ItemStack tool) {
		if (allele == null) {
			return new ArrayList<>();
		}

		Collection<ItemStack> fruits = Arrays.asList(getDrop());
		maturity = 0;
		sendNetworkUpdateRipening();
		return fruits;
	}

	@Override
	public float getRipeness() {
		return (float) maturity / MAX_MATURITY;
	}

	@Override
	public void addRipeness(float add) {
		maturity += MAX_MATURITY * add;
		sendNetworkUpdateRipening();
	}

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		data.writeShort(maturity);
		data.writeShort(indices.length);
		for (int i : indices) {
			data.writeInt(i);
		}
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		maturity = data.readShort();
		int indicesLength = data.readShort();
		indices = new int[indicesLength];
		for (int i = 0; i < indicesLength; i++) {
			indices[i] = data.readInt();
		}

		worldObj.func_147479_m(xCoord, yCoord, zCoord);
	}
}

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IFruitBearer;
import forestry.api.genetics.IFruitFamily;
import forestry.core.network.ForestryPacket;
import forestry.core.network.INetworkedEntity;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketPayload;
import forestry.core.network.PacketUpdate;
import forestry.core.proxy.Proxies;
import forestry.core.render.TextureManager;

public class TileFruitPod extends TileEntity implements INetworkedEntity, IFruitBearer {

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
	public IIcon getIcon(int metadata, int side) {
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
		return toPacket().getPacket();
	}

	@Override
	public void sendNetworkUpdate() {
		Proxies.net.sendNetworkPacket(toPacket(), xCoord, yCoord, zCoord);
	}

	private void sendNetworkUpdateRipening() {
		Proxies.net.sendNetworkPacket(new PacketUpdate(PacketIds.TILE_UPDATE, xCoord, yCoord, zCoord, maturity), xCoord, yCoord, zCoord);
	}

	private ForestryPacket toPacket() {

		PacketPayload payload = new PacketPayload(indices.length, 1);
		payload.shortPayload[0] = maturity;
		payload.intPayload = indices;

		return new PacketUpdate(PacketIds.TILE_UPDATE, xCoord, yCoord, zCoord, payload);
	}

	@Override
	public void fromPacket(ForestryPacket packetRaw) {
		PacketUpdate packet = (PacketUpdate) packetRaw;
		maturity = packet.payload.shortPayload[0];

		if (packet.payload.intPayload != null && packet.payload.intPayload.length > 0) {
			indices = packet.payload.intPayload;
		}

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
			return new ArrayList<ItemStack>();
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

}

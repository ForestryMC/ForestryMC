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

import com.mojang.authlib.GameProfile;
import forestry.api.arboriculture.ITree;
import forestry.arboriculture.genetics.Tree;
import forestry.core.interfaces.IOwnable;
import forestry.core.network.ForestryPacket;
import forestry.core.network.INetworkedEntity;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketTileNBT;
import forestry.core.proxy.Proxies;
import forestry.core.utils.EnumAccess;
import forestry.core.utils.PlayerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;

/**
 * This is the base TE class for any block that needs to contain tree genome information.
 * 
 * @author SirSengir
 */
public abstract class TileTreeContainer extends TileEntity implements INetworkedEntity, IOwnable {

	private ITree containedTree;

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		if (nbttagcompound.hasKey("ContainedTree"))
			containedTree = new Tree(nbttagcompound.getCompoundTag("ContainedTree"));
		if (nbttagcompound.hasKey("owner")) {
			owner = NBTUtil.func_152459_a(nbttagcompound.getCompoundTag("owner"));
		}

	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		if (containedTree != null) {
			NBTTagCompound subcompound = new NBTTagCompound();
			containedTree.writeToNBT(subcompound);
			nbttagcompound.setTag("ContainedTree", subcompound);
		}
		if (this.owner != null) {
			NBTTagCompound nbt = new NBTTagCompound();
			NBTUtil.func_152460_a(nbt, owner);
			nbttagcompound.setTag("owner", nbt);
		}

	}

	/* CLIENT INFORMATION */

	/* CONTAINED TREE */
	public void setTree(ITree tree) {
		this.containedTree = tree;
		if (tree != null)
			sendNetworkUpdate();
	}

	public ITree getTree() {
		return this.containedTree;
	}

	/* UPDATING */
	/**
	 * This doesn't use normal TE updates
	 */
	@Override
	public boolean canUpdate() {
		return false;
	}

	/**
	 * Leaves and saplings will implement their logic here.
	 */
	public abstract void onBlockTick();

	/* INETWORKEDENTITY */
	@Override
	public Packet getDescriptionPacket() {
		return new PacketTileNBT(PacketIds.TILE_NBT, this).getPacket();
	}

	@Override
	public void sendNetworkUpdate() {
		Proxies.net.sendNetworkPacket(new PacketTileNBT(PacketIds.TILE_NBT, this), xCoord, yCoord, zCoord);
	}

	@Override
	public void fromPacket(ForestryPacket packetRaw) {
		PacketTileNBT packet = (PacketTileNBT) packetRaw;
		this.readFromNBT(packet.getTagCompound());
		worldObj.func_147479_m(xCoord, yCoord, zCoord);
	}

	/* IOWNABLE */
	public GameProfile owner = null;

	@Override
	public boolean allowsRemoval(EntityPlayer player) {
		return true;
	}

	@Override
	public boolean allowsInteraction(EntityPlayer player) {
		return true;
	}

	@Override
	public EnumAccess getAccess() {
		return EnumAccess.SHARED;
	}

	@Override
	public boolean isOwnable() {
		return false;
	}

	@Override
	public boolean isOwned() {
		return owner != null;
	}

	@Override
	public GameProfile getOwnerProfile() {
		return owner;
	}

	@Override
	public void setOwner(EntityPlayer player) {
		this.owner = player.getGameProfile();
	}

	public void setOwner(GameProfile playername) {
		this.owner = playername;
	}

	@Override
	public boolean isOwner(EntityPlayer player) {
		return PlayerUtil.isSameGameProfile(owner, player.getGameProfile());
	}

	@Override
	public boolean switchAccessRule(EntityPlayer player) {
		return false;
	}

}

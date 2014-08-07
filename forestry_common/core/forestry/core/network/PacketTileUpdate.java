/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import net.minecraftforge.common.util.ForgeDirection;

import com.mojang.authlib.GameProfile;

import forestry.core.EnumErrorCode;
import forestry.core.gadgets.TileForestry;
import forestry.core.utils.EnumAccess;

public class PacketTileUpdate extends PacketUpdate {

	private ForgeDirection orientation = ForgeDirection.WEST;
	private EnumErrorCode errorState = EnumErrorCode.OK;

	private boolean isOwnable = false;
	private EnumAccess access = EnumAccess.SHARED;
	private GameProfile owner = null;

	public PacketTileUpdate() {
	}

	public PacketTileUpdate(TileForestry tile) {
		super(PacketIds.TILE_FORESTRY_UPDATE, tile.getPacketPayload());

		posX = tile.xCoord;
		posY = tile.yCoord;
		posZ = tile.zCoord;

		orientation = tile.getOrientation();
		errorState = tile.getErrorState();

		isOwnable = tile.isOwnable();
		access = tile.getAccess();
		owner = tile.owner;
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {

		data.writeInt(posX);
		data.writeInt(posY);
		data.writeInt(posZ);

		data.writeInt(this.orientation.ordinal());
		data.writeInt(this.errorState.ordinal());

		if (isOwnable) {
			data.writeInt(access.ordinal());
			if (owner == null) {
				data.writeBoolean(false);
			} else {
				data.writeBoolean(true);
				data.writeLong(owner.getId().getMostSignificantBits());
				data.writeLong(owner.getId().getLeastSignificantBits());
				data.writeUTF(owner.getName());
			}
		} else
			data.writeInt(-1);

		super.writeData(data);
	}

	@Override
	public void readData(DataInputStream data) throws IOException {

		posX = data.readInt();
		posY = data.readInt();
		posZ = data.readInt();

		orientation = ForgeDirection.values()[data.readInt()];
		errorState = EnumErrorCode.values()[data.readInt()];

		int ordinal = data.readInt();
		isOwnable = ordinal >= 0;
		if (isOwnable) {
			access = EnumAccess.values()[ordinal];

			if (data.readBoolean()) {
				owner = new GameProfile(new UUID(data.readLong(), data.readLong()), data.readUTF());
			} else {
				owner = null;
			}
		}

		super.readData(data);
	}

	public ForgeDirection getOrientation() {
		return this.orientation;
	}

	public EnumErrorCode getErrorState() {
		return this.errorState;
	}

	public EnumAccess getAccess() {
		return this.access;
	}

	public GameProfile getOwner() {
		return this.owner;
	}
}

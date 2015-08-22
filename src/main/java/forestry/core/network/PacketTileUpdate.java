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
package forestry.core.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import forestry.api.core.ErrorStateRegistry;
import forestry.api.core.IErrorState;
import forestry.core.EnumErrorCode;
import forestry.core.gadgets.TileForestry;
import forestry.core.utils.EnumAccess;
import net.minecraft.util.EnumFacing;

public class PacketTileUpdate extends PacketUpdate {

	private EnumFacing orientation = EnumFacing.WEST;
	private IErrorState errorState = EnumErrorCode.OK;

	private boolean isOwnable = false;
	private EnumAccess access = EnumAccess.SHARED;
	private GameProfile owner = null;

	public PacketTileUpdate() {
	}

	public PacketTileUpdate(TileForestry tile) {
		super(PacketIds.TILE_FORESTRY_UPDATE, tile.getPacketPayload());

		pos = tile.getPos();

		orientation = tile.getOrientation();
		errorState = tile.getErrorState();

		isOwnable = tile.isOwnable();
		access = tile.getAccess();
		owner = tile.getOwnerProfile();
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		super.writeData(data);

		data.writeByte(this.orientation.ordinal());
		data.writeShort(this.errorState.getID());

		// TODO: Should this really be sent to the client? Huge network cost.
		// As far as I know, only GUIs need it, and there are better ways to get the information to a GUI.
		// -CovertJaguar
		if (isOwnable) {
			data.writeByte(access.ordinal());
			if (owner == null) {
				data.writeBoolean(false);
			} else {
				data.writeBoolean(true);
				data.writeLong(owner.getId().getMostSignificantBits());
				data.writeLong(owner.getId().getLeastSignificantBits());
				data.writeUTF(owner.getName());
			}
		} else {
			data.writeInt(-1);
		}
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		super.readData(data);

		orientation = EnumFacing.getFront(data.readByte());
		errorState = ErrorStateRegistry.getErrorState(data.readShort());

		int ordinal = data.readByte();
		isOwnable = ordinal >= 0;
		if (isOwnable) {
			access = EnumAccess.values()[ordinal];

			if (data.readBoolean()) {
				owner = new GameProfile(new UUID(data.readLong(), data.readLong()), data.readUTF());
			} else {
				owner = null;
			}
		}
	}

	public EnumFacing getOrientation() {
		return this.orientation;
	}

	public IErrorState getErrorState() {
		return this.errorState;
	}

	public EnumAccess getAccess() {
		return this.access;
	}

	public GameProfile getOwner() {
		return this.owner;
	}
}

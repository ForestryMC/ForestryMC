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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.api.core.ErrorStateRegistry;
import forestry.api.core.IErrorState;
import forestry.core.gadgets.TileForestry;
import forestry.core.utils.EnumAccess;

public class PacketTileUpdate extends PacketUpdate {

	private ForgeDirection orientation = ForgeDirection.WEST;
	private final Set<IErrorState> errorStates;

	private boolean isOwnable = false;
	private EnumAccess access = EnumAccess.SHARED;
	private GameProfile owner = null;

	public PacketTileUpdate() {
		errorStates = new HashSet<IErrorState>();
	}

	public PacketTileUpdate(TileForestry tile) {
		super(PacketIds.TILE_FORESTRY_UPDATE, tile);

		orientation = tile.getOrientation();
		errorStates = tile.getErrorStates();

		isOwnable = tile.isOwnable();
		access = tile.getAccess();
		owner = tile.getOwnerProfile();
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		super.writeData(data);

		data.writeByte(orientation.ordinal());

		data.writeShort(errorStates.size());
		for (IErrorState errorState : errorStates) {
			data.writeShort(errorState.getID());
		}

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

		orientation = ForgeDirection.getOrientation(data.readByte());

		short errorStateCount = data.readShort();
		for (int i = 0; i < errorStateCount; i++) {
			short errorStateId = data.readShort();
			IErrorState errorState = ErrorStateRegistry.getErrorState(errorStateId);
			errorStates.add(errorState);
		}

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

	public ForgeDirection getOrientation() {
		return orientation;
	}

	public Set<IErrorState> getErrorStates() {
		return errorStates;
	}

	public EnumAccess getAccess() {
		return access;
	}

	public GameProfile getOwner() {
		return owner;
	}
}

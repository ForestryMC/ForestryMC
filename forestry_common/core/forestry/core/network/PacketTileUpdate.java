/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.core.EnumErrorCode;
import forestry.core.gadgets.TileForestry;
import forestry.core.utils.EnumAccess;

public class PacketTileUpdate extends PacketUpdate {

	private ForgeDirection orientation = ForgeDirection.WEST;
	private EnumErrorCode errorState = EnumErrorCode.OK;

	private boolean isOwnable = false;
	private EnumAccess access = EnumAccess.SHARED;
	private String owner = null;

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
		if (owner == null || owner.isEmpty())
			owner = "";
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
			data.writeUTF(owner);
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
			owner = data.readUTF();
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

	public String getOwner() {
		return this.owner;
	}
}

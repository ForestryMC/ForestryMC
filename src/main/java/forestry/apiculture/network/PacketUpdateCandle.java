package forestry.apiculture.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import forestry.apiculture.gadgets.TileCandle;
import forestry.core.network.PacketCoordinates;
import forestry.core.network.PacketIds;

public class PacketUpdateCandle extends PacketCoordinates {

	private int colour;
	private boolean lit;

	public PacketUpdateCandle() {

	}

	public PacketUpdateCandle(TileCandle tileCandle) {
		super(PacketIds.CANDLE, tileCandle);

		colour = tileCandle.getColour();
		lit = tileCandle.isLit();
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		super.writeData(data);
		data.writeInt(colour);
		data.writeBoolean(lit);
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		super.readData(data);
		colour = data.readInt();
		lit = data.readBoolean();
	}

	public int getColour() {
		return colour;
	}

	public boolean isLit() {
		return lit;
	}
}

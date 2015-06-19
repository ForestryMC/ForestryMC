package forestry.apiculture.network;

import java.io.IOException;

import forestry.apiculture.gadgets.TileCandle;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.PacketCoordinates;
import forestry.core.network.PacketId;

public class PacketUpdateCandle extends PacketCoordinates {

	private int colour;
	private boolean lit;

	public PacketUpdateCandle(DataInputStreamForestry data) throws IOException {
		super(data);
	}

	public PacketUpdateCandle(TileCandle tileCandle) {
		super(PacketId.CANDLE, tileCandle);

		colour = tileCandle.getColour();
		lit = tileCandle.isLit();
	}

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		data.writeInt(colour);
		data.writeBoolean(lit);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
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

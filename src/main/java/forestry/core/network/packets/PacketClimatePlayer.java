package forestry.core.network.packets;

import forestry.api.climate.IClimateState;
import forestry.core.ClimateHandlerClient;
import forestry.core.network.*;
import net.minecraft.entity.player.PlayerEntity;

public class PacketClimatePlayer extends ForestryPacket implements IForestryPacketClient {

    private final IClimateState climateState;

    public PacketClimatePlayer(IClimateState climateState) {
        this.climateState = climateState;
    }

    @Override
    protected void writeData(PacketBufferForestry data) {
        data.writeClimateState(climateState);
    }

    @Override
    public PacketIdClient getPacketId() {
        return PacketIdClient.CLIMATE_PLAYER;
    }

    public static class Handler implements IForestryPacketHandlerClient {
        @Override
        public void onPacketData(PacketBufferForestry data, PlayerEntity player) {
            ClimateHandlerClient.setCurrentState(data.readClimateState());
        }
    }
}

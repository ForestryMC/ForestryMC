package forestry.core.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;

@OnlyIn(Dist.CLIENT)
public interface IForestryPacketHandlerClient extends IForestryPacketHandler {
    void onPacketData(PacketBufferForestry data, PlayerEntity player) throws IOException;
}

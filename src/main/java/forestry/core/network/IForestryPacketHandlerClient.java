package forestry.core.network;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IForestryPacketHandlerClient extends IForestryPacketHandler {
	void onPacketData(PacketBufferForestry data, EntityPlayer player) throws IOException;
}

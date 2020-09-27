package forestry.sorting.network.packets;

import forestry.api.genetics.GeneticCapabilities;
import forestry.api.genetics.filter.IFilterLogic;
import forestry.core.network.*;
import forestry.core.tiles.TileUtil;
import forestry.sorting.tiles.IFilterContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;

public class PacketGuiFilterUpdate extends ForestryPacket implements IForestryPacketClient {
    private final BlockPos pos;
    private final IFilterLogic logic;

    public PacketGuiFilterUpdate(IFilterContainer container) {
        this.pos = container.getCoordinates();
        this.logic = container.getLogic();
    }

    @Override
    protected void writeData(PacketBufferForestry data) {
        data.writeBlockPos(pos);
        logic.writeGuiData(data);
    }

    @Override
    public PacketIdClient getPacketId() {
        return PacketIdClient.GUI_UPDATE;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Handler implements IForestryPacketHandlerClient {
        @Override
        public void onPacketData(PacketBufferForestry data, PlayerEntity player) {
            BlockPos pos = data.readBlockPos();

            LazyOptional<IFilterLogic> logic = TileUtil.getInterface(
                    player.world,
                    pos,
                    GeneticCapabilities.FILTER_LOGIC,
                    null
            );
            logic.ifPresent(l -> l.readGuiData(data));
        }
    }
}

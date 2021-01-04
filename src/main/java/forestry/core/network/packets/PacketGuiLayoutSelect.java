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
package forestry.core.network.packets;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuitLayout;
import forestry.core.circuits.ContainerSolderingIron;
import forestry.core.network.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PacketGuiLayoutSelect extends ForestryPacket implements IForestryPacketClient {
    private final String layoutUid;

    public PacketGuiLayoutSelect(String layoutUid) {
        this.layoutUid = layoutUid;
    }

    @Override
    public PacketIdClient getPacketId() {
        return PacketIdClient.GUI_LAYOUT_SELECT;
    }

    @Override
    protected void writeData(PacketBufferForestry data) {
        data.writeString(layoutUid);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Handler implements IForestryPacketHandlerClient {
        @Override
        public void onPacketData(PacketBufferForestry data, PlayerEntity player) {
            String layoutUid = data.readString();
            Container container = player.openContainer;
            if (!(container instanceof ContainerSolderingIron)) {
                return;
            }

            ICircuitLayout layout = ChipsetManager.circuitRegistry.getLayout(layoutUid);
            if (layout != null) {
                ((ContainerSolderingIron) container).setLayout(layout);
            }
        }
    }
}

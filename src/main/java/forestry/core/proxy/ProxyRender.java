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
package forestry.core.proxy;

import net.minecraftforge.client.event.ModelBakeEvent;

import forestry.core.blocks.MachinePropertiesTesr;
import forestry.core.tiles.TileAnalyzer;
import forestry.core.tiles.TileBase;
import forestry.core.tiles.TileEscritoire;
import forestry.core.tiles.TileMill;
import forestry.core.tiles.TileNaturalistChest;
import forestry.modules.ISidedModuleHandler;

public class ProxyRender implements ISidedModuleHandler {

    public boolean fancyGraphicsEnabled() {
        return false;
    }

    public void initRendering() {
    }

    public void setRenderDefaultMachine(MachinePropertiesTesr<? extends TileBase> machineProperties, String baseTexture) {
    }

    public void setRenderMill(MachinePropertiesTesr<? extends TileMill> machineProperties, String baseTexture) {
    }

    public void setRenderEscritoire(MachinePropertiesTesr<? extends TileEscritoire> machineProperties) {
    }

    public void setRendererAnalyzer(MachinePropertiesTesr<? extends TileAnalyzer> machineProperties) {
    }

    public void setRenderChest(MachinePropertiesTesr<? extends TileNaturalistChest> machineProperties, String textureName) {
    }

    public void registerModels(ModelBakeEvent event) {
    }

    public void registerItemAndBlockColors() {
    }

}

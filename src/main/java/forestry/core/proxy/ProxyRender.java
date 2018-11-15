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

import net.minecraft.block.Block;

import forestry.core.blocks.MachinePropertiesTesr;
import forestry.core.fluids.Fluids;
import forestry.core.tiles.TileAnalyzer;
import forestry.core.tiles.TileBase;
import forestry.core.tiles.TileEscritoire;
import forestry.core.tiles.TileMill;
import forestry.core.tiles.TileNaturalistChest;

public class ProxyRender {

	public boolean fancyGraphicsEnabled() {
		return false;
	}

	public void initRendering() {
	}

	public void setRenderDefaultMachine(MachinePropertiesTesr<? extends TileBase> machineProperties, String gfxBase) {
	}

	public void setRenderMill(MachinePropertiesTesr<? extends TileMill> machineProperties, String gfxBase) {
	}

	public void setRenderMill(MachinePropertiesTesr<? extends TileMill> machineProperties, String gfxBase, byte charges) {
	}

	public void setRenderEscritoire(MachinePropertiesTesr<? extends TileEscritoire> machineProperties) {
	}

	public void setRendererAnalyzer(MachinePropertiesTesr<? extends TileAnalyzer> machineProperties) {
	}

	public void setRenderChest(MachinePropertiesTesr<? extends TileNaturalistChest> machineProperties, String textureName) {
	}

	public void registerModels() {
	}

	public void registerItemAndBlockColors() {
	}

	public void registerFluidStateMapper(Block block, Fluids fluid) {
	}

}

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

import forestry.core.fluids.Fluids;
import forestry.core.gadgets.MachineDefinition;
import forestry.core.interfaces.IBlockRenderer;
import forestry.core.render.BlockModelIndex;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.statemap.IStateMapper;

public class ProxyRender {

	public boolean fancyGraphicsEnabled() {
		return false;
	}

	public boolean hasRendering() {
		return false;
	}

	public void registerTESR(MachineDefinition definition) {
	}

	public IBlockRenderer getRenderDefaultMachine(String gfxBase) {
		return null;
	}

	public IBlockRenderer getRenderMill(String gfxBase) {
		return null;
	}

	public IBlockRenderer getRenderMill(String gfxBase, byte charges) {
		return null;
	}

	public IBlockRenderer getRenderEscritoire() {
		return null;
	}

	public void registerBlockModel(BlockModelIndex index) {
	}

	public void registerStateMapper(Block block, IStateMapper mapper) {
	}

	public void registerFluidStateMapper(Block block, Fluids forestryFluid) {
	}

	public short registerItemTexUID(String modifier, short uid, String ident) {
		return uid;
	}

	public short registerTerrainTexUID(String modifier, short uid, String ident) {
		return uid;
	}

	public void registerVillagerSkin(int villagerId, String texturePath) {
	}

	public void preInitModels() {
	}

	public void initModels() {
	}
}

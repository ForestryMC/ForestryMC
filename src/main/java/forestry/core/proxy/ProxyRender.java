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

import net.minecraft.client.renderer.texture.IIconRegister;

import forestry.core.gadgets.MachineDefinition;
import forestry.core.interfaces.IBlockRenderer;

public class ProxyRender {

	public int getNextAvailableRenderId() {
		return 0;
	}

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

	public short registerItemTexUID(IIconRegister register, short uid, String ident) {
		return uid;
	}

	public short registerTerrainTexUID(IIconRegister register, short uid, String ident) {
		return uid;
	}

	public void registerVillagerSkin(int villagerId, String texturePath) {
	}
}

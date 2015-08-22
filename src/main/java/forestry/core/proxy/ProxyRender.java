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

import forestry.core.gadgets.MachineDefinition;
import forestry.core.interfaces.IBlockRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.world.World;

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

	public void addSnowFX(World world, double xCoord, double yCoord, double zCoord, int color, int areaX, int areaY, int areaZ) {
	}

	public short registerItemTexUID(TextureMap map, short uid, String ident) {
		return uid;
	}

	public short registerTerrainTexUID(TextureMap map, short uid, String ident) {
		return uid;
	}

	public void registerVillagerSkin(int villagerId, String texturePath) {
	}
}

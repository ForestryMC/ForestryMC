/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.proxy;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.world.World;

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

	public void addSnowFX(World world, double xCoord, double yCoord, double zCoord, int color, int areaX, int areaY, int areaZ) {
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

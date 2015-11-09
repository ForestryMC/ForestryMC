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

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import forestry.core.render.IBlockRenderer;
import forestry.core.render.SpriteSheet;
import forestry.core.tiles.MachineDefinition;

public class ProxyRender {

	public void init() {
	}

	public int getCandleRenderId() {
		return 0;
	}

	public int getByBlockModelRenderId() {
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

	public IBlockRenderer getRenderChest(String textureName) {
		return null;
	}

	public void registerVillagerSkin(int villagerId, String texturePath) {
	}

	public void setHabitatLocatorTexture(Entity player, ChunkCoordinates coordinates) {
	}

	public IResourceManager getSelectedTexturePack() {
		return null;
	}

	public void bindTexture(ResourceLocation location) {
	}

	public void bindTexture(SpriteSheet spriteSheet) {
	}

	/* FX */

	public void addBeeHiveFX(String texture, World world, double d1, double d2, double d3, int color) {
	}

	public void addEntitySwarmFX(World world, double d1, double d2, double d3) {
	}

	public void addEntityExplodeFX(World world, double d1, double d2, double d3) {
	}

	public void addEntitySnowFX(World world, double d1, double d2, double d3) {
	}

	public void addEntityIgnitionFX(World world, double d1, double d2, double d3) {
	}

	public void addEntityPotionFX(World world, double d1, double d2, double d3, int color) {
	}
}

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
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import forestry.core.fluids.Fluids;
import forestry.core.render.model.BlockModelIndex;
import forestry.core.tiles.MachineDefinition;

public class ProxyRender {

	public void init() {
	}

	public boolean fancyGraphicsEnabled() {
		return false;
	}

	public boolean hasRendering() {
		return false;
	}

	public void registerTESR(MachineDefinition definition) {
	}

	public TileEntitySpecialRenderer getRenderDefaultMachine(String gfxBase) {
		return null;
	}

	public TileEntitySpecialRenderer getRenderMill(String gfxBase) {
		return null;
	}

	public TileEntitySpecialRenderer getRenderMill(String gfxBase, byte charges) {
		return null;
	}

	public TileEntitySpecialRenderer getRenderEscritoire() {
		return null;
	}

	public TileEntitySpecialRenderer getRenderChest(String textureName) {
		return null;
	}
	
	public void registerBlockModel(BlockModelIndex index) {
	}

	public void registerStateMapper(Block block, IStateMapper mapper) {
	}

	public void registerFluidStateMapper(Block block, Fluids forestryFluid) {
	}

	public void registerVillagerSkin(int villagerId, String texturePath) {
	}

	public void setHabitatLocatorTexture(Entity player, BlockPos coordinates) {
	}

	public IResourceManager getSelectedTexturePack() {
		return null;
	}

	public void bindTexture(ResourceLocation location) {
	}
	
	public void preInitModels() {
	}

	public void initModels() {
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

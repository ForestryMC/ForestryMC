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
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.b3d.B3DLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.client.FMLClientHandler;

import javax.annotation.Nonnull;

import forestry.apiculture.entities.EntityFXBee;
import forestry.apiculture.render.ParticleRenderer;
import forestry.apiculture.render.TextureHabitatLocator;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.entities.EntityFXHoneydust;
import forestry.core.entities.EntityFXIgnition;
import forestry.core.entities.EntityFXSnow;
import forestry.core.fluids.Fluids;
import forestry.core.models.BlockModelIndex;
import forestry.core.models.ModelIndex;
import forestry.core.models.ModelManager;
import forestry.core.render.RenderAnalyzer;
import forestry.core.render.RenderEscritoire;
import forestry.core.render.RenderMachine;
import forestry.core.render.RenderMill;
import forestry.core.render.RenderNaturalistChest;
import forestry.core.render.TextureManager;
import forestry.core.tiles.TileAnalyzer;
import forestry.core.tiles.TileBase;
import forestry.core.tiles.TileEscritoire;
import forestry.core.tiles.TileMill;
import forestry.core.tiles.TileNaturalistChest;

public class ProxyRenderClient extends ProxyRender {

	@Override
	public boolean fancyGraphicsEnabled() {
		return Proxies.common.getClientInstance().gameSettings.fancyGraphics;
	}

	@Override
	public boolean hasRendering() {
		return true;
	}

	@Override
	public TileEntitySpecialRenderer<TileBase> getRenderDefaultMachine(String gfxBase) {
		if (gfxBase == null) {
			return null;
		}
		return new RenderMachine(gfxBase);
	}

	@Override
	public TileEntitySpecialRenderer<TileMill> getRenderMill(String gfxBase) {
		return new RenderMill(gfxBase);
	}

	@Override
	public TileEntitySpecialRenderer<TileMill> getRenderMill(String gfxBase, byte charges) {
		return new RenderMill(gfxBase, charges);
	}

	@Override
	public TileEntitySpecialRenderer<TileEscritoire> getRenderEscritoire() {
		return new RenderEscritoire();
	}

	@Override
	public TileEntitySpecialRenderer<TileAnalyzer> getRendererAnalyzer() {
		return new RenderAnalyzer(Constants.TEXTURE_PATH_BLOCKS + "/analyzer_");
	}

	@Override
	public TileEntitySpecialRenderer<TileNaturalistChest> getRenderChest(String textureName) {
		return new RenderNaturalistChest(textureName);
	}

	@Override
	public void setHabitatLocatorTexture(Entity player, BlockPos pos) {
		TextureHabitatLocator.getInstance().setTargetCoordinates(pos);
	}

	@Override
	public IResourceManager getSelectedTexturePack() {
		return Proxies.common.getClientInstance().getResourceManager();
	}

	@Override
	public void bindTexture(ResourceLocation location) {
		Proxies.common.getClientInstance().getTextureManager().bindTexture(location);
	}
	
	@Override
	public void registerBlockModel(@Nonnull final BlockModelIndex index) {
		ModelManager.registerCustomBlockModel(index);
		StateMapperBase ignoreState = new BlockModeStateMapper(index);
		registerStateMapper(index.block, ignoreState);
	}
	
	@Override
	public void registerModel(@Nonnull ModelIndex index) {
		ModelManager.registerCustomModel(index);
	}

	@Override
	public void registerFluidStateMapper(Block block, final Fluids forestryFluid) {
		final ModelResourceLocation fluidLocation = new ModelResourceLocation("forestry:blockforestryfluid",
				forestryFluid.getTag());
		StateMapperBase ignoreState = new FluidStateMapper(fluidLocation);
		registerStateMapper(block, ignoreState);
		ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(block), new FluidItemMeshDefinition(fluidLocation));
		ModelBakery.registerItemVariants(Item.getItemFromBlock(block), fluidLocation);
	}

	@Override
	public void registerStateMapper(Block block, IStateMapper mapper) {
		ModelLoader.setCustomStateMapper(block, mapper);
	}
	
	@Override
	public void registerLoaders(){
		B3DLoader.instance.addDomain(Constants.ID);
		OBJLoader.instance.addDomain(Constants.ID);
	}

	@Override
	public void registerModels() {
		ModelManager.registerModels();
	}

	public static boolean shouldSpawnParticle(World world) {
		if (!Config.enableParticleFX) {
			return false;
		}

		Minecraft mc = FMLClientHandler.instance().getClient();
		int particleSetting = mc.gameSettings.particleSetting;

		// minimal
		if (particleSetting == 2) {
			return world.rand.nextInt(10) == 0;
		}

		// decreased
		if (particleSetting == 1) {
			return world.rand.nextInt(3) != 0;
		}

		// all
		return true;
	}

	@Override
	public void addBeeHiveFX(String icon, World world, double d1, double d2, double d3, int color) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		EntityFX fx = new EntityFXBee(world, d1, d2, d3, color);
		fx.setParticleIcon(TextureManager.getInstance().getDefault(icon));
		ParticleRenderer.getInstance().addEffect(fx);
	}

	@Override
	public void addEntitySwarmFX(World world, double d1, double d2, double d3) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		Proxies.common.getClientInstance().effectRenderer.addEffect(new EntityFXHoneydust(world, d1, d2, d3, 0, 0, 0));
	}

	@Override
	public void addEntityExplodeFX(World world, double d1, double d2, double d3) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		EntityFX entityfx = Proxies.common.getClientInstance().effectRenderer.spawnEffectParticle(EnumParticleTypes.EXPLOSION_NORMAL.getParticleID(), d1, d2, d3, 0, 0, 0);
		Proxies.common.getClientInstance().effectRenderer.addEffect(entityfx);
	}

	@Override
	public void addEntitySnowFX(World world, double d1, double d2, double d3) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		Proxies.common.getClientInstance().effectRenderer.addEffect(new EntityFXSnow(world, d1 + world.rand.nextGaussian(), d2, d3 + world.rand.nextGaussian()));
	}

	@Override
	public void addEntityIgnitionFX(World world, double d1, double d2, double d3) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		Proxies.common.getClientInstance().effectRenderer.addEffect(new EntityFXIgnition(world, d1, d2, d3));
	}

	@Override
	public void addEntityPotionFX(World world, double d1, double d2, double d3, int color) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		float red = (color >> 16 & 255) / 255.0F;
		float green = (color >> 8 & 255) / 255.0F;
		float blue = (color & 255) / 255.0F;
		
		EntityFX entityfx = Proxies.common.getClientInstance().effectRenderer.spawnEffectParticle(EnumParticleTypes.SPELL.getParticleID(), d1, d2, d3, 0, 0, 0);
		entityfx.setRBGColorF(red, green, blue);

		Proxies.common.getClientInstance().effectRenderer.addEffect(entityfx);
	}

	private static class BlockModeStateMapper extends StateMapperBase {
		private final BlockModelIndex index;

		public BlockModeStateMapper(BlockModelIndex index) {
			this.index = index;
		}

		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState iBlockState) {
			return index.blockModelLocation;
		}
	}

	private static class FluidStateMapper extends StateMapperBase {
		private final ModelResourceLocation fluidLocation;

		public FluidStateMapper(ModelResourceLocation fluidLocation) {
			this.fluidLocation = fluidLocation;
		}

		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState iBlockState) {
			return fluidLocation;
		}
	}

	private static class FluidItemMeshDefinition implements ItemMeshDefinition {
		private final ModelResourceLocation fluidLocation;

		public FluidItemMeshDefinition(ModelResourceLocation fluidLocation) {
			this.fluidLocation = fluidLocation;
		}

		@Override
		public ModelResourceLocation getModelLocation(ItemStack stack) {
			return fluidLocation;
		}
	}
}

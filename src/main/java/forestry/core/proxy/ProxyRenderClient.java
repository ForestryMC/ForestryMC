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

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
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
		return Minecraft.getMinecraft().gameSettings.fancyGraphics;
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
		return Minecraft.getMinecraft().getResourceManager();
	}

	@Override
	public void bindTexture(ResourceLocation location) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(location);
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
	public void registerModels() {
		ModelManager.registerModels();
	}

	private static boolean shouldSpawnParticle(World world) {
		if (!Config.enableParticleFX) {
			return false;
		}

		Minecraft mc = Minecraft.getMinecraft();
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
	public void addBeeHiveFX(String icon, World world, double x, double y, double z, int color) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		EntityFX fx = new EntityFXBee(world, x, y, z, color);
		TextureAtlasSprite sprite = TextureManager.getInstance().getDefault(icon);
		fx.setParticleIcon(sprite);
		ParticleRenderer.getInstance().addEffect(fx);
	}

	@Override
	public void addEntityHoneyDustFX(World world, double x, double y, double z) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		EffectRenderer effectRenderer = Minecraft.getMinecraft().effectRenderer;
		effectRenderer.addEffect(new EntityFXHoneydust(world, x, y, z, 0, 0, 0));
	}

	@Override
	public void addEntityExplodeFX(World world, double x, double y, double z) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		EffectRenderer effectRenderer = Minecraft.getMinecraft().effectRenderer;
		EntityFX entityfx = effectRenderer.spawnEffectParticle(EnumParticleTypes.EXPLOSION_NORMAL.getParticleID(), x, y, z, 0, 0, 0);
		effectRenderer.addEffect(entityfx);
	}

	@Override
	public void addEntitySnowFX(World world, double x, double y, double z) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		EffectRenderer effectRenderer = Minecraft.getMinecraft().effectRenderer;
		effectRenderer.addEffect(new EntityFXSnow(world, x + world.rand.nextGaussian(), y, z + world.rand.nextGaussian()));
	}

	@Override
	public void addEntityIgnitionFX(World world, double x, double y, double z) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		EffectRenderer effectRenderer = Minecraft.getMinecraft().effectRenderer;
		effectRenderer.addEffect(new EntityFXIgnition(world, x, y, z));
	}

	@Override
	public void addEntityPotionFX(World world, double x, double y, double z, int color) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		float red = (color >> 16 & 255) / 255.0F;
		float green = (color >> 8 & 255) / 255.0F;
		float blue = (color & 255) / 255.0F;
		
		EffectRenderer effectRenderer = Minecraft.getMinecraft().effectRenderer;
		EntityFX entityfx = effectRenderer.spawnEffectParticle(EnumParticleTypes.SPELL.getParticleID(), x, y, z, 0, 0, 0);
		entityfx.setRBGColorF(red, green, blue);

		effectRenderer.addEffect(entityfx);
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

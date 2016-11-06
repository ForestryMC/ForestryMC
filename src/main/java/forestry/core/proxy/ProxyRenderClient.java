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
import java.util.List;

import com.google.common.collect.ImmutableMap;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IHiveTile;
import forestry.apiculture.entities.ParticleBeeExplore;
import forestry.apiculture.entities.ParticleBeeRoundTrip;
import forestry.apiculture.entities.ParticleBeeTargetEntity;
import forestry.apiculture.genetics.alleles.AlleleEffect;
import forestry.apiculture.render.TextureHabitatLocator;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.entities.ParticleHoneydust;
import forestry.core.entities.ParticleIgnition;
import forestry.core.entities.ParticleSmoke;
import forestry.core.entities.ParticleSnow;
import forestry.core.fluids.Fluids;
import forestry.core.models.BlockModelEntry;
import forestry.core.models.ModelEntry;
import forestry.core.models.ModelManager;
import forestry.core.render.RenderAnalyzer;
import forestry.core.render.RenderEscritoire;
import forestry.core.render.RenderMachine;
import forestry.core.render.RenderMill;
import forestry.core.render.RenderNaturalistChest;
import forestry.core.render.TextureManager;
import forestry.core.render.TextureMapForestry;
import forestry.core.tiles.TileAnalyzer;
import forestry.core.tiles.TileBase;
import forestry.core.tiles.TileEscritoire;
import forestry.core.tiles.TileMill;
import forestry.core.tiles.TileNaturalistChest;
import forestry.core.utils.VectUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.animation.ITimeValue;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;

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
	public void initRendering() {
		TextureManager textureManager = TextureManager.getInstance();
		TextureMapForestry textureMap = textureManager.getTextureMap();

		Minecraft minecraft = Minecraft.getMinecraft();
		minecraft.renderEngine.loadTickableTexture(TextureManager.getInstance().getGuiTextureMap(), textureMap);
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
	public void registerBlockModel(@Nonnull final BlockModelEntry index) {
		ModelManager.getInstance().registerCustomBlockModel(index);
		if(index.addStateMapper){
			StateMapperBase ignoreState = new BlockModeStateMapper(index);
			registerStateMapper(index.block, ignoreState);
		}
	}
	
	@Override
	public void registerModel(@Nonnull ModelEntry index) {
		ModelManager.getInstance().registerCustomModel(index);
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
		ModelManager.getInstance().registerModels();
	}

	@Override
	public void registerItemAndBlockColors() {
		ModelManager.getInstance().registerItemAndBlockColors();
	}

	@Override
    public IAnimationStateMachine loadAnimationState(ResourceLocation location, ImmutableMap<String, ITimeValue> parameters){
		return ModelLoaderRegistry.loadASM(location, parameters);
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
	public void addBeeHiveFX(@Nonnull IBeeHousing housing, @Nonnull IBeeGenome genome, @Nonnull List<BlockPos> flowerPositions) {
		World world = housing.getWorldObj();
		if (!shouldSpawnParticle(world)) {
			return;
		}

		ParticleManager effectRenderer = Minecraft.getMinecraft().effectRenderer;

		Vec3d particleStart = housing.getBeeFXCoordinates();

		// Avoid rendering bee particles that are too far away, they're very small.
		// At 32+ distance, have no bee particles. Make more particles up close.
		BlockPos playerPosition = Proxies.common.getPlayer().getPosition();
		double playerDistanceSq = playerPosition.distanceSqToCenter(particleStart.xCoord, particleStart.yCoord, particleStart.zCoord);
		if (world.rand.nextInt(1024) < playerDistanceSq) {
			return;
		}

		int color = genome.getPrimary().getSpriteColour(0);

		if (!flowerPositions.isEmpty()) {
			int randomInt = world.rand.nextInt(100);

			if (housing instanceof IHiveTile) {
				if (((IHiveTile) housing).isAngry() || randomInt >= 85) {
					List<EntityLivingBase> entitiesInRange = AlleleEffect.getEntitiesInRange(genome, housing, EntityLivingBase.class);
					if (!entitiesInRange.isEmpty()) {
						EntityLivingBase entity = entitiesInRange.get(world.rand.nextInt(entitiesInRange.size()));
						Particle particle = new ParticleBeeTargetEntity(world, particleStart, entity, color);
						effectRenderer.addEffect(particle);
						return;
					}
				}
			}

			if (randomInt < 75) {
				BlockPos destination = flowerPositions.get(world.rand.nextInt(flowerPositions.size()));
				Particle particle = new ParticleBeeRoundTrip(world, particleStart, destination, color);
				effectRenderer.addEffect(particle);
			} else {
				Vec3i area = AlleleEffect.getModifiedArea(genome, housing);
				Vec3i offset = housing.getCoordinates().add(-area.getX() / 2, -area.getY() / 4, -area.getZ() / 2);
				BlockPos destination = VectUtil.getRandomPositionInArea(world.rand, area).add(offset);
				Particle particle = new ParticleBeeExplore(world, particleStart, destination, color);
				effectRenderer.addEffect(particle);
			}
		}
	}

	@Override
	public void addEntityHoneyDustFX(World world, double x, double y, double z) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		ParticleManager effectRenderer = Minecraft.getMinecraft().effectRenderer;
		effectRenderer.addEffect(new ParticleHoneydust(world, x, y, z, 0, 0, 0));
	}

	@Override
	public void addEntityExplodeFX(World world, double x, double y, double z) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		ParticleManager effectRenderer = Minecraft.getMinecraft().effectRenderer;
		Particle Particle = effectRenderer.spawnEffectParticle(EnumParticleTypes.EXPLOSION_NORMAL.getParticleID(), x, y, z, 0, 0, 0);
		effectRenderer.addEffect(Particle);
	}

	@Override
	public void addEntitySnowFX(World world, double x, double y, double z) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		ParticleManager effectRenderer = Minecraft.getMinecraft().effectRenderer;
		effectRenderer.addEffect(new ParticleSnow(world, x + world.rand.nextGaussian(), y, z + world.rand.nextGaussian()));
	}

	@Override
	public void addEntityIgnitionFX(World world, double x, double y, double z) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		ParticleManager effectRenderer = Minecraft.getMinecraft().effectRenderer;
		effectRenderer.addEffect(new ParticleIgnition(world, x, y, z));
	}

	@Override
	public void addEntitySmokeFX(World world, double x, double y, double z) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		ParticleManager effectRenderer = Minecraft.getMinecraft().effectRenderer;
		effectRenderer.addEffect(new ParticleSmoke(world, x, y, z));
	}

	@Override
	public void addEntityPotionFX(World world, double x, double y, double z, int color) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		float red = (color >> 16 & 255) / 255.0F;
		float green = (color >> 8 & 255) / 255.0F;
		float blue = (color & 255) / 255.0F;
		
		ParticleManager effectRenderer = Minecraft.getMinecraft().effectRenderer;
		Particle Particle = effectRenderer.spawnEffectParticle(EnumParticleTypes.SPELL.getParticleID(), x, y, z, 0, 0, 0);
		Particle.setRBGColorF(red, green, blue);

		effectRenderer.addEffect(Particle);
	}

	private static class BlockModeStateMapper extends StateMapperBase {
		private final BlockModelEntry index;

		public BlockModeStateMapper(BlockModelEntry index) {
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

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
import javax.annotation.Nullable;
import java.util.List;

import com.google.common.collect.ImmutableMap;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.core.fluids.Fluids;
import forestry.core.models.BlockModelEntry;
import forestry.core.models.ModelEntry;
import forestry.core.tiles.TileAnalyzer;
import forestry.core.tiles.TileBase;
import forestry.core.tiles.TileEscritoire;
import forestry.core.tiles.TileMill;
import forestry.core.tiles.TileNaturalistChest;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.animation.ITimeValue;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;

public class ProxyRender {

	public boolean fancyGraphicsEnabled() {
		return false;
	}

	public boolean hasRendering() {
		return false;
	}

	public void initRendering() {

	}

	@Nullable
	public TileEntitySpecialRenderer<TileBase> getRenderDefaultMachine(String gfxBase) {
		return null;
	}

	@Nullable
	public TileEntitySpecialRenderer<TileMill> getRenderMill(String gfxBase) {
		return null;
	}

	@Nullable
	public TileEntitySpecialRenderer<TileMill> getRenderMill(String gfxBase, byte charges) {
		return null;
	}

	@Nullable
	public TileEntitySpecialRenderer<TileEscritoire> getRenderEscritoire() {
		return null;
	}

	@Nullable
	public TileEntitySpecialRenderer<TileAnalyzer> getRendererAnalyzer() {
		return null;
	}

	@Nullable
	public TileEntitySpecialRenderer<TileNaturalistChest> getRenderChest(String textureName) {
		return null;
	}

	public void registerBlockModel(BlockModelEntry index) {
	}

	public void registerModel(ModelEntry index) {
	}

	public void registerStateMapper(Block block, IStateMapper mapper) {
	}

	public void registerFluidStateMapper(Block block, Fluids fluid) {
	}

	public void setHabitatLocatorTexture(Entity player, BlockPos pos) {
	}

	public IResourceManager getSelectedTexturePack() {
		return null;
	}

	public void bindTexture(ResourceLocation location) {
	}

	public void registerModels() {
	}

	public void registerItemAndBlockColors() {
	}

	public IAnimationStateMachine loadAnimationState(ResourceLocation location, ImmutableMap<String, ITimeValue> parameters) {
		return null;
	}

	/* FX */

	public void addBeeHiveFX(@Nonnull IBeeHousing housing, @Nonnull IBeeGenome genome, @Nonnull List<BlockPos> flowerPositions) {
	}

	public void addEntityHoneyDustFX(World world, double x, double y, double z) {
	}

	public void addEntityExplodeFX(World world, double x, double y, double z) {
	}

	public void addEntitySnowFX(World world, double x, double y, double z) {
	}

	public void addEntityIgnitionFX(World world, double x, double y, double z) {
	}

	public void addEntitySmokeFX(World world, double x, double y, double z) {
	}

	public void addEntityPotionFX(World world, double x, double y, double z, int color) {
	}
}

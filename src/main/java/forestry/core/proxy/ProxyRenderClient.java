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
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AtlasTexture;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.blocks.MachinePropertiesTesr;
import forestry.core.config.Constants;
import forestry.core.fluids.ForestryFluids;
import forestry.core.models.ClientManager;
import forestry.core.render.RenderAnalyzer;
import forestry.core.render.RenderEscritoire;
import forestry.core.render.RenderMachine;
import forestry.core.render.RenderMill;
import forestry.core.render.RenderNaturalistChest;
import forestry.core.render.TextureManagerForestry;
import forestry.core.tiles.TileAnalyzer;
import forestry.core.tiles.TileBase;
import forestry.core.tiles.TileEscritoire;
import forestry.core.tiles.TileMill;
import forestry.core.tiles.TileNaturalistChest;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class ProxyRenderClient extends ProxyRender {

	@Override
	public boolean fancyGraphicsEnabled() {
		return Minecraft.getInstance().gameSettings.fancyGraphics;
	}

	@Override
	public void initRendering() {
		TextureManagerForestry textureManagerForestry = TextureManagerForestry.getInstance();
		AtlasTexture textureMap = textureManagerForestry.getTextureMap();

		Minecraft minecraft = Minecraft.getInstance();
		minecraft.getTextureManager().loadTickableTexture(TextureManagerForestry.getInstance().getGuiTextureMap(), textureMap);
	}

	@Override
	public void setRenderDefaultMachine(MachinePropertiesTesr<? extends TileBase> machineProperties, String gfxBase) {
		machineProperties.setRenderer(new RenderMachine(gfxBase));
	}

	@Override
	public void setRenderMill(MachinePropertiesTesr<? extends TileMill> machineProperties, String gfxBase) {
		machineProperties.setRenderer(new RenderMill(gfxBase));
	}

	@Override
	public void setRenderMill(MachinePropertiesTesr<? extends TileMill> machineProperties, String gfxBase, byte charges) {
		machineProperties.setRenderer(new RenderMill(gfxBase, charges));
	}

	@Override
	public void setRenderEscritoire(MachinePropertiesTesr<? extends TileEscritoire> machineProperties) {
		machineProperties.setRenderer(new RenderEscritoire());
	}

	@Override
	public void setRendererAnalyzer(MachinePropertiesTesr<? extends TileAnalyzer> machineProperties) {
		RenderAnalyzer renderAnalyzer = new RenderAnalyzer(Constants.TEXTURE_PATH_BLOCK + "/analyzer");
		machineProperties.setRenderer(renderAnalyzer);
	}

	@Override
	public void setRenderChest(MachinePropertiesTesr<? extends TileNaturalistChest> machineProperties, String textureName) {
		machineProperties.setRenderer(new RenderNaturalistChest(textureName));
	}

	@Override
	public void registerItemAndBlockColors() {
		ClientManager.getInstance().registerItemAndBlockColors();
	}

	@Override
	public void registerFluidStateMapper(Block block, ForestryFluids fluid) {
		//		final ModelResourceLocation fluidLocation = new ModelResourceLocation("forestry:blockforestryfluid", fluid.getTag());
		//TODO - fluids
		// StateMapperBase ignoreState = new FluidStateMapper(fluidLocation);
		//		ModelLoader.setCustomStateMapper(block, ignoreState);
		//		ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(block), new FluidItemMeshDefinition(fluidLocation));
		//		ModelBakery.registerItemVariants(Item.getItemFromBlock(block), fluidLocation);
	}

	//	private static class FluidStateMapper extends StateMapperBase {
	//		private final ModelResourceLocation fluidLocation;
	//
	//		public FluidStateMapper(ModelResourceLocation fluidLocation) {
	//			this.fluidLocation = fluidLocation;
	//		}
	//
	//		@Override
	//		protected ModelResourceLocation getModelResourceLocation(BlockState BlockState) {
	//			return fluidLocation;
	//		}
	//	}

	//	private static class FluidItemMeshDefinition implements ItemMeshDefinition {
	//		private final ModelResourceLocation fluidLocation;
	//
	//		public FluidItemMeshDefinition(ModelResourceLocation fluidLocation) {
	//			this.fluidLocation = fluidLocation;
	//		}
	//
	//		@Override
	//		public ModelResourceLocation getModelLocation(ItemStack stack) {
	//			return fluidLocation;
	//		}
	//	}
}

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

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.settings.GraphicsFanciness;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import forestry.core.blocks.IBlockType;
import forestry.core.blocks.IMachineProperties;
import forestry.core.blocks.IMachinePropertiesTesr;
import forestry.core.blocks.MachinePropertiesTesr;
import forestry.core.config.Constants;
import forestry.core.features.CoreBlocks;
import forestry.core.items.definitions.EnumContainerType;
import forestry.core.models.ClientManager;
import forestry.core.models.FluidContainerModel;
import forestry.core.render.RenderAnalyzer;
import forestry.core.render.RenderEscritoire;
import forestry.core.render.RenderForestryItem;
import forestry.core.render.RenderMachine;
import forestry.core.render.RenderMill;
import forestry.core.render.RenderNaturalistChest;
import forestry.core.tiles.TileAnalyzer;
import forestry.core.tiles.TileBase;
import forestry.core.tiles.TileEscritoire;
import forestry.core.tiles.TileMill;
import forestry.core.tiles.TileNaturalistChest;
import forestry.modules.IClientModuleHandler;

public class ProxyRenderClient extends ProxyRender implements IClientModuleHandler {

	@Override
	public boolean fancyGraphicsEnabled() {
		return Minecraft.getInstance().options.graphicsMode == GraphicsFanciness.FANCY;
	}

	@Override
	public void setupClient(FMLClientSetupEvent event) {
		for (EnumContainerType type : EnumContainerType.values()) {
			ModelLoader.addSpecialModel(new ModelResourceLocation("forestry:" + type.getSerializedName() + "_empty", "inventory"));
			ModelLoader.addSpecialModel(new ModelResourceLocation("forestry:" + type.getSerializedName() + "_filled", "inventory"));
		}
		CoreBlocks.BASE.getBlocks().forEach((block) -> RenderTypeLookup.setRenderLayer(block, RenderType.cutoutMipped()));
	}

	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoaderRegistry.registerLoader(new ResourceLocation(Constants.MOD_ID, "fluid_container"), new FluidContainerModel.Loader());
	}

	@Override
	public void registerModels(ModelBakeEvent event) {
		ClientManager.getInstance().onBakeModels(event);
	}

	@Override
	public void setRenderDefaultMachine(MachinePropertiesTesr<? extends TileBase> machineProperties, String baseTexture) {
		machineProperties.setRenderer(new RenderMachine(baseTexture));
	}

	@Override
	public void setRenderMill(MachinePropertiesTesr<? extends TileMill> machineProperties, String baseTexture) {
		machineProperties.setRenderer(new RenderMill(baseTexture));
	}

	@Override
	public void setRenderEscritoire(MachinePropertiesTesr<? extends TileEscritoire> machineProperties) {
		machineProperties.setRenderer(new RenderEscritoire());
	}

	@Override
	public void setRendererAnalyzer(MachinePropertiesTesr<? extends TileAnalyzer> machineProperties) {
		RenderAnalyzer renderAnalyzer = new RenderAnalyzer();
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
	public void setRenderer(Item.Properties properties, IBlockType type) {
		IMachineProperties<?> machineProperties = type.getMachineProperties();
		if (!(machineProperties instanceof IMachinePropertiesTesr)) {
			return;
		}
		IMachinePropertiesTesr<?> machinePropertiesTesr = (IMachinePropertiesTesr<?>) machineProperties;
		if (machinePropertiesTesr.getRenderer() == null) {
			return;
		}
		properties.setISTER(() -> () -> new RenderForestryItem(machinePropertiesTesr.getRenderer()));
	}


}

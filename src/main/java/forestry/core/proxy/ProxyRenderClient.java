/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.core.proxy;

import forestry.core.blocks.MachinePropertiesTesr;
import forestry.core.config.Constants;
import forestry.core.features.CoreBlocks;
import forestry.core.items.EnumContainerType;
import forestry.core.models.ClientManager;
import forestry.core.models.FluidContainerModel;
import forestry.core.render.*;
import forestry.core.tiles.*;
import forestry.modules.IClientModuleHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.settings.GraphicsFanciness;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class ProxyRenderClient extends ProxyRender implements IClientModuleHandler {

    @Override
    public boolean fancyGraphicsEnabled() {
        return Minecraft.getInstance().gameSettings.graphicFanciness == GraphicsFanciness.FANCY;
    }

    @Override
    public void registerModels(ModelRegistryEvent event) {
        for (EnumContainerType type : EnumContainerType.values()) {
            ModelLoader.addSpecialModel(new ModelResourceLocation(
                    "forestry:" + type.getString() + "_empty",
                    "inventory"
            ));
            ModelLoader.addSpecialModel(new ModelResourceLocation(
                    "forestry:" + type.getString() + "_filled",
                    "inventory"
            ));
        }
        CoreBlocks.BASE.getBlocks()
                       .forEach((block) -> RenderTypeLookup.setRenderLayer(block, RenderType.getCutoutMipped()));
        ModelLoaderRegistry.registerLoader(
                new ResourceLocation(Constants.MOD_ID, "fluid_container"),
                new FluidContainerModel.Loader()
        );
    }

    @Override
    public void setRenderDefaultMachine(
            MachinePropertiesTesr<? extends TileBase> machineProperties,
            String baseTexture
    ) {
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
    public void setRenderChest(
            MachinePropertiesTesr<? extends TileNaturalistChest> machineProperties,
            String textureName
    ) {
        machineProperties.setRenderer(new RenderNaturalistChest(textureName));
    }

    @Override
    public void registerItemAndBlockColors() {
        ClientManager.getInstance().registerItemAndBlockColors();
    }
}

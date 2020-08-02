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
import net.minecraft.client.settings.GraphicsFanciness;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import forestry.core.blocks.MachinePropertiesTesr;
import forestry.core.features.CoreBlocks;
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
import forestry.modules.IClientModuleHandler;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class ProxyRenderClient extends ProxyRender implements IClientModuleHandler {

    @Override
    public boolean fancyGraphicsEnabled() {
        return Minecraft.getInstance().gameSettings.field_238330_f_ == GraphicsFanciness.FANCY;
    }

    @Override
    public void initRendering() {
        TextureManagerForestry textureManagerForestry = TextureManagerForestry.getInstance();

        Minecraft minecraft = Minecraft.getInstance();
        //minecraft.getTextureManager().loadTickableTexture(TextureManagerForestry.getInstance().getGuiTextureMap(), textureMap);// TODO: Gui atlas
    }

    @Override
    public void setupClient(FMLClientSetupEvent event) {
        CoreBlocks.BASE.getBlocks().forEach((block) -> RenderTypeLookup.setRenderLayer(block, RenderType.getCutoutMipped()));
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
}

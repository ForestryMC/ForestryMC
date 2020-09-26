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
package forestry.climatology.gui.elements;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import forestry.api.climate.IClimateState;
import forestry.api.climate.IClimateTransformer;
import forestry.api.core.ForestryAPI;
import forestry.climatology.gui.GuiHabitatFormer;
import forestry.core.climate.ClimateStateHelper;
import forestry.core.config.Constants;
import forestry.core.gui.elements.GuiElement;
import forestry.core.gui.elements.layouts.ElementGroup;
import forestry.core.gui.elements.lib.events.GuiEvent;
import forestry.core.render.TextureManagerForestry;
import forestry.core.utils.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class HabitatSelectionElement extends ElementGroup {
    private static final Comparator<ClimateButton> BUTTON_COMPARATOR = Comparator.comparingDouble(ClimateButton::getComparingCode);
    private final List<ClimateButton> buttons = new ArrayList<>();
    private final IClimateTransformer transformer;

    public HabitatSelectionElement(int xPos, int yPos, IClimateTransformer transformer) {
        super(xPos, yPos, 60, 40);
        this.transformer = transformer;
        int x = 0;
        int y = 0;
        for (EnumClimate climate : EnumClimate.values()) {
            ClimateButton button = new ClimateButton(climate, x * 20, y * 20);
            buttons.add(button);
            add(button);
            x++;
            if (x >= 3) {
                y++;
                x = 0;
            }
        }
    }

    @Override
    public void drawElement(MatrixStack transform, int mouseY, int mouseX) {
        super.drawElement(transform, mouseY, mouseX);
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        textureManager.bindTexture(new ResourceLocation(Constants.MOD_ID, "textures/gui/habitat_former.png"));
        Optional<ClimateButton> optional = buttons.stream().min(BUTTON_COMPARATOR);
        if (!optional.isPresent()) {
            return;
        }
        ClimateButton button = optional.get();
        blit(transform, button.getX() - 1, button.getY() - 1, 0, 233, 22, 22);
    }

    private enum EnumClimate {
        ICY("habitats/snow", ((Supplier<Biome>) Biomes.SNOWY_TUNDRA).get()),
        COLD("habitats/taiga", ((Supplier<Biome>) Biomes.TAIGA).get()),
        HILLS("habitats/hills", ((Supplier<Biome>) Biomes.SWAMP).get()),
        NORMAL("habitats/plains", ((Supplier<Biome>) Biomes.PLAINS).get()),
        WARM("habitats/jungle", ((Supplier<Biome>) Biomes.JUNGLE).get()),
        HOT("habitats/desert", ((Supplier<Biome>) Biomes.DESERT).get());
        private final IClimateState climateState;
        private final String spriteName;

        EnumClimate(String spriteName, Biome biome) {
            climateState = ClimateStateHelper.of(biome.getTemperature(), biome.getDownfall());
            this.spriteName = spriteName;
        }

        @OnlyIn(Dist.CLIENT)
        public TextureAtlasSprite getSprite() {
            return ForestryAPI.textureManager.getDefault(spriteName);
        }
    }

    private class ClimateButton extends GuiElement {
        final EnumClimate climate;

        private ClimateButton(EnumClimate climate, int xPos, int yPos) {
            super(xPos, yPos, 20, 20);
            this.climate = climate;
            addSelfEventHandler(GuiEvent.DownEvent.class, event -> {
                IClimateState climateState = climate.climateState;
                GuiHabitatFormer former = (GuiHabitatFormer) getWindow().getGui();
                former.setClimate(climateState);
                former.sendClimateUpdate();
            });
            addTooltip((tooltip, element, mouseX, mouseY) -> {
                tooltip.add(new StringTextComponent("T: " + StringUtil.floatAsPercent(climate.climateState.getTemperature())));
                tooltip.add(new StringTextComponent("H: " + StringUtil.floatAsPercent(climate.climateState.getHumidity())));
            });
        }

        @Override
        public void drawElement(MatrixStack transform, int mouseY, int mouseX) {
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0F);
            TextureManager textureManager = Minecraft.getInstance().getTextureManager();
            textureManager.bindTexture(new ResourceLocation(Constants.MOD_ID, "textures/gui/habitat_former.png"));
            blit(transform, 0, 0, 204, 46, 20, 20);
            TextureManagerForestry.getInstance().bindGuiTextureMap();
            blit(transform, 2, 2, getBlitOffset(), 16, 16, climate.getSprite());
        }

        private double getComparingCode() {
            IClimateState target = transformer.getTarget();
            IClimateState state = climate.climateState;
            double temp = target.getTemperature() - state.getTemperature();
            double hem = target.getHumidity() - state.getHumidity();
            return Math.abs(temp + hem);
        }
    }
}

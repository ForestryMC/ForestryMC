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

import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import genetics.api.GeneticsAPI;
import genetics.api.individual.IIndividual;

import forestry.api.climate.IClimateTransformer;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.IAlleleForestrySpecies;
import forestry.api.gui.events.GuiEvent;
import forestry.core.climate.ClimateStateHelper;
import forestry.core.config.Constants;
import forestry.core.gui.elements.GuiElement;

@OnlyIn(Dist.CLIENT)
public class SpeciesSelectionElement extends GuiElement {
	public SpeciesSelectionElement(int xPos, int yPos, IClimateTransformer transformer) {
		super(xPos, yPos, 22, 22);
		addSelfEventHandler(GuiEvent.DownEvent.class, event -> {
			PlayerEntity player = Minecraft.getInstance().player;
			ItemStack itemstack = player.inventory.getItemStack();
			if (itemstack.isEmpty()) {
				return;
			}
			Optional<IIndividual> optional = GeneticsAPI.apiInstance.getRootHelper().getIndividual(itemstack);
			if (!optional.isPresent()) {
				return;
			}
			IIndividual individual = optional.get();
			IAlleleForestrySpecies primary = individual.getGenome().getPrimary(IAlleleForestrySpecies.class);
			EnumTemperature temperature = primary.getTemperature();
			EnumHumidity humidity = primary.getHumidity();
			float temp;
			float humid;
			switch (temperature) {
				case HELLISH:
					temp = 2.0F;
					break;
				case HOT:
					temp = 1.25F;
					break;
				case WARM:
					temp = 0.9F;
					break;
				case COLD:
					temp = 0.15F;
					break;
				case ICY:
					temp = 0.0F;
					break;
				case NORMAL:
				default:
					temp = 0.79F;
					break;
			}
			switch (humidity) {
				case DAMP:
					humid = 0.9F;
					break;
				case ARID:
					humid = 0.2F;
					break;
				case NORMAL:
				default:
					humid = 0.4F;
					break;
			}
			transformer.setTarget(ClimateStateHelper.INSTANCE.create(temp, humid));
		});
	}

	@Override
	public void drawElement(int mouseX, int mouseY) {
		super.drawElement(mouseX, mouseY);
		TextureManager textureManager = Minecraft.getInstance().getTextureManager();
		textureManager.bindTexture(new ResourceLocation(Constants.MOD_ID, "textures/gui/habitat_former.png"));
		GlStateManager.enableAlphaTest();
		blit(0, 0, 224, 46, 22, 22);
		GlStateManager.disableAlphaTest();
	}

	@Override
	public boolean canMouseOver() {
		return true;
	}
}

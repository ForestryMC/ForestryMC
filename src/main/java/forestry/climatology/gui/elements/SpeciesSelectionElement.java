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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.climate.IClimateTransformer;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.alleles.IAlleleForestrySpecies;
import forestry.core.climate.ClimateStateHelper;
import forestry.core.config.Constants;
import forestry.core.gui.elements.GuiElement;

import genetics.api.individual.IIndividual;
import genetics.utils.RootUtils;

@OnlyIn(Dist.CLIENT)
public class SpeciesSelectionElement extends GuiElement {

	private final IClimateTransformer transformer;

	public SpeciesSelectionElement(int xPos, int yPos, IClimateTransformer transformer) {
		super(xPos, yPos);
		setSize(22, 22);
		this.transformer = transformer;
		/*addSelfEventHandler(GuiEvent.DownEvent.class, event -> {
			PlayerEntity player = Minecraft.getInstance().player;
			ItemStack itemstack = player.inventoryMenu.getCarried();
			if (itemstack.isEmpty()) {
				return;
			}
			Optional<IIndividual> optional = RootUtils.getIndividual(itemstack);
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
		});*/
	}

	@Override
	public boolean onMouseClicked(double mouseX, double mouseY, int mouseButton) {
		Player player = Minecraft.getInstance().player;
		ItemStack itemstack = player.inventoryMenu.getCarried();
		if (itemstack.isEmpty()) {
			return false;
		}
		Optional<IIndividual> optional = RootUtils.getIndividual(itemstack);
		if (!optional.isPresent()) {
			return false;
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
		return true;
	}

	@Override
	public void drawElement(PoseStack transform, int mouseX, int mouseY) {
		super.drawElement(transform, mouseX, mouseY);
		TextureManager textureManager = Minecraft.getInstance().getTextureManager();
		textureManager.bind(new ResourceLocation(Constants.MOD_ID, "textures/gui/habitat_former.png"));
		RenderSystem.enableAlphaTest();
		blit(transform, 0, 0, 224, 46, 22, 22);
		RenderSystem.disableAlphaTest();
	}

	@Override
	public boolean canMouseOver() {
		return true;
	}
}

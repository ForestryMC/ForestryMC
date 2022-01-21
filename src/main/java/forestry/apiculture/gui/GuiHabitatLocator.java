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
package forestry.apiculture.gui;

import com.google.common.collect.LinkedListMultimap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.biome.Biome;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraftforge.registries.ForgeRegistries;

import forestry.apiculture.inventory.ItemInventoryHabitatLocator;
import forestry.core.config.Constants;
import forestry.core.gui.GuiForestry;
import forestry.core.render.ColourProperties;
import forestry.core.utils.Translator;

public class GuiHabitatLocator extends GuiForestry<ContainerHabitatLocator> {
	private static final LinkedListMultimap<String, Biome.BiomeCategory> habitats = LinkedListMultimap.create();

	static {
		habitats.putAll("Ocean", Arrays.asList(Biome.BiomeCategory.OCEAN, Biome.BiomeCategory.BEACH));
		habitats.put("Plains", Biome.BiomeCategory.PLAINS);
		habitats.put("Desert", Biome.BiomeCategory.DESERT);
		habitats.putAll("Forest", Arrays.asList(Biome.BiomeCategory.FOREST, Biome.BiomeCategory.RIVER));
		habitats.put("Jungle", Biome.BiomeCategory.JUNGLE);
		habitats.put("Taiga", Biome.BiomeCategory.TAIGA);
		habitats.put("Hills", Biome.BiomeCategory.EXTREME_HILLS);
		habitats.put("Swamp", Biome.BiomeCategory.SWAMP);
		habitats.put("Snow", Biome.BiomeCategory.ICY);
		habitats.put("Mushroom", Biome.BiomeCategory.MUSHROOM);
		habitats.put("Nether", Biome.BiomeCategory.NETHER);
		habitats.put("End", Biome.BiomeCategory.THEEND);
	}

	private final ItemInventoryHabitatLocator itemInventory;
	private final List<HabitatSlot> habitatSlots = new ArrayList<>(habitats.size());

	private int startX;
	private int startY;

	public GuiHabitatLocator(ContainerHabitatLocator container, Inventory playerInv, Component title) {
		super(Constants.TEXTURE_PATH_GUI + "/biomefinder.png", container, playerInv, title);

		this.itemInventory = container.getItemInventory();
		imageWidth = 176;
		imageHeight = 184;

		int slot = 0;
		for (String habitatName : habitats.keySet()) {
			int x;
			int y;
			if (slot > 5) {
				x = 18 + (slot - 6) * 20;
				y = 50;
			} else {
				x = 18 + slot * 20;
				y = 32;
			}
			Collection<Biome.BiomeCategory> biomes = habitats.get(habitatName);
			HabitatSlot habitatSlot = new HabitatSlot(widgetManager, x, y, habitatName, biomes);
			habitatSlots.add(habitatSlot);
			widgetManager.add(habitatSlot);
			slot++;
		}
	}


	@Override
	protected void renderBg(PoseStack transform, float partialTicks, int mouseY, int mouseX) {
		//super.renderBg(transform, partialTicks, mouseY, mouseX);

		String str = Translator.translateToLocal("item.forestry.habitat_locator").toUpperCase(Locale.ENGLISH);
		getFontRenderer().draw(transform, str, startX + 8 + textLayout.getCenteredOffset(str, 138), startY + 16, ColourProperties.INSTANCE.get("gui.screen"));

		// Set active according to valid biomes.
		Set<Biome.BiomeCategory> activeBiomeTypes = new HashSet<>();
		for (ResourceLocation biomeLocation : itemInventory.getBiomesToSearch()) {
			Biome biome = ForgeRegistries.BIOMES.getValue(biomeLocation);
			if (biome == null) {
				continue;
			}
			Biome.BiomeCategory biomeTypes = biome.getBiomeCategory();
			activeBiomeTypes.add(biomeTypes);
		}

		for (HabitatSlot habitatSlot : habitatSlots) {
			habitatSlot.setActive(activeBiomeTypes);
		}

		for (HabitatSlot slot : habitatSlots) {
			slot.draw(transform, startY, startX);
		}
		RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f); // Reset afterwards.
	}

	@Override
	public void init() {
		super.init();

		startX = (this.width - this.imageWidth) / 2;
		startY = (this.height - this.imageHeight) / 2;
	}

	@Override
	protected void addLedgers() {
		addErrorLedger(itemInventory);
		addHintLedger("habitat.locator");
	}
}

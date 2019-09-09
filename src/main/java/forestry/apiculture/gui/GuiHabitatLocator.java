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

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.biome.Biome;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraftforge.common.BiomeDictionary;

import forestry.apiculture.inventory.ItemInventoryHabitatLocator;
import forestry.core.config.Constants;
import forestry.core.gui.GuiForestry;
import forestry.core.render.ColourProperties;
import forestry.core.utils.Translator;

public class GuiHabitatLocator extends GuiForestry<ContainerHabitatLocator> {
	private static final LinkedListMultimap<String, BiomeDictionary.Type> habitats = LinkedListMultimap.create();

	static {
		habitats.putAll("Ocean", Arrays.asList(BiomeDictionary.Type.OCEAN, BiomeDictionary.Type.BEACH));
		habitats.put("Plains", BiomeDictionary.Type.PLAINS);
		habitats.put("Desert", BiomeDictionary.Type.SANDY);
		habitats.putAll("Forest", Arrays.asList(BiomeDictionary.Type.FOREST, BiomeDictionary.Type.RIVER));
		habitats.put("Jungle", BiomeDictionary.Type.JUNGLE);
		habitats.put("Taiga", BiomeDictionary.Type.CONIFEROUS);
		habitats.put("Hills", BiomeDictionary.Type.MOUNTAIN);
		habitats.put("Swamp", BiomeDictionary.Type.SWAMP);
		habitats.put("Snow", BiomeDictionary.Type.SNOWY);
		habitats.put("Mushroom", BiomeDictionary.Type.MUSHROOM);
		habitats.put("Nether", BiomeDictionary.Type.NETHER);
		habitats.put("End", BiomeDictionary.Type.END);
	}

	private final ItemInventoryHabitatLocator itemInventory;
	private final List<HabitatSlot> habitatSlots = new ArrayList<>(habitats.size());

	private int startX;
	private int startY;

	public GuiHabitatLocator(ContainerHabitatLocator container, PlayerInventory playerInv, ITextComponent title) {
		super(Constants.TEXTURE_PATH_GUI + "/biomefinder.png", container, playerInv, title);

		this.itemInventory = container.getItemInventory();
		xSize = 176;
		ySize = 184;

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
			Collection<BiomeDictionary.Type> biomes = habitats.get(habitatName);
			HabitatSlot habitatSlot = new HabitatSlot(widgetManager, x, y, habitatName, biomes);
			habitatSlots.add(habitatSlot);
			widgetManager.add(habitatSlot);
			slot++;
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		String str = Translator.translateToLocal("item.forestry.habitat_locator").toUpperCase(Locale.ENGLISH);
		getFontRenderer().drawString(str, startX + 8 + textLayout.getCenteredOffset(str, 138), startY + 16, ColourProperties.INSTANCE.get("gui.screen"));

		// Set active according to valid biomes.
		Set<BiomeDictionary.Type> activeBiomeTypes = new HashSet<>();
		for (Biome biome : itemInventory.getBiomesToSearch()) {
			Set<BiomeDictionary.Type> biomeTypes = BiomeDictionary.getTypes(biome);
			activeBiomeTypes.addAll(biomeTypes);
		}

		for (HabitatSlot habitatSlot : habitatSlots) {
			habitatSlot.setActive(activeBiomeTypes);
		}

		for (HabitatSlot slot : habitatSlots) {
			slot.draw(startX, startY);
		}
		GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f); // Reset afterwards.
	}

	@Override
	public void init() {
		super.init();

		startX = (this.width - this.xSize) / 2;
		startY = (this.height - this.ySize) / 2;
	}

	@Override
	protected void addLedgers() {
		addErrorLedger(itemInventory);
		addHintLedger("habitat.locator");
	}
}

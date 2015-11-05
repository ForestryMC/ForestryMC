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

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.biome.BiomeGenBase;

import net.minecraftforge.common.BiomeDictionary;

import org.lwjgl.opengl.GL11;

import forestry.apiculture.inventory.ItemInventoryHabitatLocator;
import forestry.core.config.Constants;
import forestry.core.gui.GuiForestry;
import forestry.core.gui.widgets.Widget;
import forestry.core.proxy.Proxies;
import forestry.core.render.SpriteSheet;
import forestry.core.render.TextureManager;

public class GuiHabitatLocator extends GuiForestry<ContainerHabitatLocator, ItemInventoryHabitatLocator> {

	private final HabitatSlot[] habitatSlots = new HabitatSlot[]{
			new HabitatSlot(0, "Ocean"), // ocean, beach
			new HabitatSlot(1, "Plains"),
			new HabitatSlot(2, "Desert"), // desert, desert hills
			new HabitatSlot(3, "Forest"), // forest, forestHills, river
			new HabitatSlot(4, "Jungle"), // jungle, jungleHills
			new HabitatSlot(5, "Taiga"), // taiga, taigaHills
			new HabitatSlot(6, "Hills"), // extremeHills, extremeHillsEdge
			new HabitatSlot(7, "Swamp"),
			new HabitatSlot(8, "Snow"), // Ice plains, mountains, frozen rivers, frozen oceans
			new HabitatSlot(9, "Mushroom"),
			new HabitatSlot(10, "Nether"),
			new HabitatSlot(11, "End")};
	private final Map<BiomeDictionary.Type, HabitatSlot> biomeToHabitat = new EnumMap<>(BiomeDictionary.Type.class);

	private int startX;
	private int startY;

	public GuiHabitatLocator(EntityPlayer player, ItemInventoryHabitatLocator item) {
		super(Constants.TEXTURE_PATH_GUI + "/biomefinder.png", new ContainerHabitatLocator(player, item), item);

		xSize = 176;
		ySize = 184;

		int x;
		int y;
		for (HabitatSlot slot : habitatSlots) {

			if (slot.slot > 5) {
				x = 18 + (slot.slot - 6) * 20;
				y = 50;
			} else {
				x = 18 + slot.slot * 20;
				y = 32;
			}

			slot.setPosition(x, y);
			this.widgetManager.add(slot);
		}

		biomeToHabitat.put(BiomeDictionary.Type.OCEAN, habitatSlots[0]);
		biomeToHabitat.put(BiomeDictionary.Type.BEACH, habitatSlots[0]);
		biomeToHabitat.put(BiomeDictionary.Type.PLAINS, habitatSlots[1]);
		biomeToHabitat.put(BiomeDictionary.Type.SANDY, habitatSlots[2]);
		biomeToHabitat.put(BiomeDictionary.Type.FOREST, habitatSlots[3]);
		biomeToHabitat.put(BiomeDictionary.Type.RIVER, habitatSlots[3]);
		biomeToHabitat.put(BiomeDictionary.Type.JUNGLE, habitatSlots[4]);
		biomeToHabitat.put(BiomeDictionary.Type.CONIFEROUS, habitatSlots[5]);
		biomeToHabitat.put(BiomeDictionary.Type.MOUNTAIN, habitatSlots[6]);
		biomeToHabitat.put(BiomeDictionary.Type.SWAMP, habitatSlots[7]);
		biomeToHabitat.put(BiomeDictionary.Type.SNOWY, habitatSlots[8]);
		biomeToHabitat.put(BiomeDictionary.Type.MUSHROOM, habitatSlots[9]);
		biomeToHabitat.put(BiomeDictionary.Type.NETHER, habitatSlots[10]);
		biomeToHabitat.put(BiomeDictionary.Type.END, habitatSlots[11]);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		String str = StatCollector.translateToLocal("item.for.habitatLocator.name").toUpperCase();
		fontRendererObj.drawString(str, startX + 8 + getCenteredOffset(str, 138), startY + 16, fontColor.get("gui.screen"));

		// Set active according to valid biomes.
		Set<BiomeDictionary.Type> activeBiomeTypes = EnumSet.noneOf(BiomeDictionary.Type.class);

		for (HabitatSlot habitatSlot : habitatSlots) {
			habitatSlot.isActive = false;
		}

		for (BiomeGenBase biome : inventory.getBiomesToSearch()) {
			Collections.addAll(activeBiomeTypes, BiomeDictionary.getTypesForBiome(biome));
		}

		for (BiomeDictionary.Type biomeType : activeBiomeTypes) {
			HabitatSlot habitatSlot = biomeToHabitat.get(biomeType);
			if (habitatSlot != null) {
				habitatSlot.isActive = true;
			}
		}

		for (HabitatSlot slot : habitatSlots) {
			slot.draw(startX, startY);
		}
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f); // Reset afterwards.

	}

	@Override
	public void initGui() {
		super.initGui();

		startX = (this.width - this.xSize) / 2;
		startY = (this.height - this.ySize) / 2;
	}

	public class HabitatSlot extends Widget {

		private final int slot;
		private final String name;
		private final String iconIndex;
		public boolean isActive = false;

		public HabitatSlot(int slot, String name) {
			super(widgetManager, 0, 0);
			this.slot = slot;
			this.name = name;
			this.iconIndex = "habitats/" + name.toLowerCase(Locale.ENGLISH);
		}

		@Override
		public String getLegacyTooltip(EntityPlayer player) {
			return name;
		}

		public IIcon getIcon() {
			return TextureManager.getInstance().getDefault(iconIndex);
		}

		public void setPosition(int x, int y) {
			this.xPos = x;
			this.yPos = y;
		}

		@Override
		public void draw(int startX, int startY) {

			if (getIcon() != null) {
				GL11.glDisable(GL11.GL_LIGHTING);

				if (!isActive) {
					GL11.glColor4f(0.2f, 0.2f, 0.2f, 0.2f);
				} else {
					GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
				}

				Proxies.render.bindTexture(SpriteSheet.ITEMS);
				manager.gui.drawTexturedModelRectFromIcon(startX + xPos, startY + yPos, getIcon(), 16, 16);
				GL11.glEnable(GL11.GL_LIGHTING);
			}
		}

	}

}

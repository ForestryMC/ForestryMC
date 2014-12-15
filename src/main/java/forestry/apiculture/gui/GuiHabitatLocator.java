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

import forestry.apiculture.items.ItemBiomefinder.BiomefinderInventory;
import forestry.core.config.Defaults;
import forestry.core.gadgets.TileForestry;
import forestry.core.gui.GuiForestry;
import forestry.core.gui.widgets.Widget;
import forestry.core.proxy.Proxies;
import forestry.core.render.SpriteSheet;
import forestry.core.render.TextureManager;
import forestry.core.utils.StringUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.biome.BiomeGenBase;
import org.lwjgl.opengl.GL11;

public class GuiHabitatLocator extends GuiForestry<TileForestry> {

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

				if (!isActive)
					GL11.glColor4f(0.2f, 0.2f, 0.2f, 0.2f);

				Proxies.common.bindTexture(SpriteSheet.ITEMS);
				manager.gui.drawTexturedModelRectFromIcon(startX + xPos, startY + yPos, getIcon(), 16, 16);
				GL11.glEnable(GL11.GL_LIGHTING);
			}
		}

	}

	private final HabitatSlot[] habitatSlots = new HabitatSlot[] { new HabitatSlot(0, "Ocean"), // ocean
			// +
			// beach
			new HabitatSlot(1, "Plains"), new HabitatSlot(2, "Desert"), // desert
			// +
			// desert
			// hills
			new HabitatSlot(3, "Forest"), // forest, forestHills, river
			new HabitatSlot(4, "Jungle"), // jungle, jungleHills
			new HabitatSlot(5, "Taiga"), // taiga, taigaHills
			new HabitatSlot(6, "Hills"), // extremeHills, extremeHillsEdge
			new HabitatSlot(7, "Swampland"), new HabitatSlot(8, "Snow"), // Ice
			// plains,
			// mountains,
			// frozen
			// rivers,
			// frozen
			// oceans
			new HabitatSlot(9, "Mushroom"), // Ice plains, mountains, frozen
			// rivers, frozen oceans
			new HabitatSlot(10, "Hell"), new HabitatSlot(11, "End") };
	private final HashMap<Integer, HabitatSlot> biomeToHabitat = new HashMap<Integer, HabitatSlot>();

	private int startX;
	private int startY;

	public GuiHabitatLocator(InventoryPlayer inventory, BiomefinderInventory item) {
		super(Defaults.TEXTURE_PATH_GUI + "/biomefinder.png", new ContainerHabitatLocator(inventory, item), item);

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

		biomeToHabitat.put(BiomeGenBase.ocean.biomeID, habitatSlots[0]);
		biomeToHabitat.put(BiomeGenBase.beach.biomeID, habitatSlots[0]);
		biomeToHabitat.put(BiomeGenBase.plains.biomeID, habitatSlots[1]);
		biomeToHabitat.put(BiomeGenBase.desert.biomeID, habitatSlots[2]);
		// biomeToHabitat.put(BiomeGenBase.desertHills.biomeID,
		// habitatSlots[2]); // Removed for TFC compatibility
		biomeToHabitat.put(BiomeGenBase.forest.biomeID, habitatSlots[3]);
		biomeToHabitat.put(BiomeGenBase.forestHills.biomeID, habitatSlots[3]);
		biomeToHabitat.put(BiomeGenBase.river.biomeID, habitatSlots[3]);
		biomeToHabitat.put(BiomeGenBase.jungle.biomeID, habitatSlots[4]);
		biomeToHabitat.put(BiomeGenBase.jungleHills.biomeID, habitatSlots[4]);
		biomeToHabitat.put(BiomeGenBase.taiga.biomeID, habitatSlots[5]);
		biomeToHabitat.put(BiomeGenBase.taigaHills.biomeID, habitatSlots[5]);
		biomeToHabitat.put(BiomeGenBase.extremeHills.biomeID, habitatSlots[6]);
		biomeToHabitat.put(BiomeGenBase.extremeHillsEdge.biomeID, habitatSlots[6]);
		biomeToHabitat.put(BiomeGenBase.swampland.biomeID, habitatSlots[7]);
		biomeToHabitat.put(BiomeGenBase.frozenOcean.biomeID, habitatSlots[8]);
		biomeToHabitat.put(BiomeGenBase.frozenRiver.biomeID, habitatSlots[8]);
		biomeToHabitat.put(BiomeGenBase.iceMountains.biomeID, habitatSlots[8]);
		biomeToHabitat.put(BiomeGenBase.icePlains.biomeID, habitatSlots[8]);
		biomeToHabitat.put(BiomeGenBase.mushroomIsland.biomeID, habitatSlots[9]);
		biomeToHabitat.put(BiomeGenBase.mushroomIslandShore.biomeID, habitatSlots[9]);
		biomeToHabitat.put(BiomeGenBase.hell.biomeID, habitatSlots[10]);
		biomeToHabitat.put(BiomeGenBase.sky.biomeID, habitatSlots[11]);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		String str = StringUtil.localize("gui.habitatlocator").toUpperCase();
		fontRendererObj.drawString(str, startX + 8 + getCenteredOffset(str, 138), startY + 16, fontColor.get("gui.screen"));

		str = "(" + StringUtil.localize("gui.closetosearch") + ")";
		fontRendererObj.drawString(str, startX + 8 + getCenteredOffset(str, 138), startY + 76, fontColor.get("gui.table.header"));

		// Reset habitat slots
		for (HabitatSlot slot : habitatSlots)
			slot.isActive = false;

		// Set according to valid biomes.
		ArrayList<Integer> biomeids = ((ContainerHabitatLocator) inventorySlots).inventory.biomesToSearch;
		for (int biomeid : biomeids) {
			if (!biomeToHabitat.containsKey(biomeid))
				continue;

			biomeToHabitat.get(biomeid).isActive = true;
		}

		for (HabitatSlot slot : habitatSlots)
			slot.draw(startX, startY);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f); // Reset afterwards.

	}

	@Override
	public void initGui() {
		super.initGui();

		startX = (this.width - this.xSize) / 2;
		startY = (this.height - this.ySize) / 2;
	}

}

/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.pipes.gui;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import buildcraft.transport.Pipe;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IApiaristTracker;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.apiculture.items.ItemBeeGE;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.genetics.ItemGE;
import forestry.core.gui.widgets.Widget;
import forestry.core.gui.GuiForestry;
import forestry.core.proxy.Proxies;
import forestry.core.render.SpriteSheet;
import forestry.core.utils.StringUtil;
import forestry.pipes.EnumFilterType;
import forestry.pipes.PipeItemsPropolis;
import forestry.pipes.PipeLogicPropolis;
import forestry.plugins.PluginApiculture;

/**
 * GuiScreen for propolis pipes.
 * 
 * @author SirSengir
 */
public class GuiPropolisPipe extends GuiForestry {

	class TypeFilterSlot extends Widget {

		ForgeDirection orientation;
		PipeLogicPropolis logic;

		public TypeFilterSlot(int x, int y, ForgeDirection orientation, PipeLogicPropolis logic) {
			super(widgetManager, x, y);
			this.orientation = orientation;
			this.logic = logic;
		}

		public EnumFilterType getType() {
			return logic.getTypeFilter(orientation);
		}

		@Override
		public void draw(int startX, int startY) {
			EnumFilterType type = logic.getTypeFilter(orientation);
			IIcon icon = null;
			if (type != null)
				icon = type.getIcon();
			if(icon == null)
				return;

			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0F);
			Proxies.common.bindTexture(SpriteSheet.ITEMS);
			drawTexturedModelRectFromIcon(startX + xPos, startY + yPos, icon, 16, 16);

		}

		@Override
		protected String getLegacyTooltip(EntityPlayer player) {
			EnumFilterType type = logic.getTypeFilter(orientation);
			return StringUtil.localize("gui.pipe.filter." + type.toString().toLowerCase(Locale.ENGLISH));
		}

		@Override
		public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
			EnumFilterType change;
			if (mouseButton == 1)
				change = EnumFilterType.CLOSED;
			else if (getType().ordinal() < EnumFilterType.values().length - 1)
				change = EnumFilterType.values()[getType().ordinal() + 1];
			else
				change = EnumFilterType.CLOSED;
			pipeLogic.setTypeFilter(orientation, change);

		}

	}

	class SpeciesFilterSlot extends Widget {

		IApiaristTracker tracker;
		ForgeDirection orientation;
		PipeLogicPropolis logic;
		int pattern;
		int allele;

		public SpeciesFilterSlot(IApiaristTracker tracker, int x, int y, ForgeDirection orientation, int pattern, int allele, PipeLogicPropolis logic) {
			super(widgetManager, x, y);
			this.tracker = tracker;
			this.orientation = orientation;
			this.pattern = pattern;
			this.allele = allele;
			this.logic = logic;
		}

		public IAlleleSpecies getSpecies() {
			return logic.getSpeciesFilter(orientation, pattern, allele);
		}

		public boolean isDefined() {
			IAlleleSpecies species = logic.getSpeciesFilter(orientation, pattern, allele);
			return species != null;
		}

		@Override
		public void draw(int startX, int startY) {
			if (!isDefined())
				return;

			IAlleleSpecies species = logic.getSpeciesFilter(orientation, pattern, allele);
			GL11.glDisable(GL11.GL_LIGHTING);

			for (int i = 0; i < 3; ++i) {

				IIcon icon = ((ItemBeeGE) ForestryItem.beeDroneGE.item()).getIconFromSpecies((IAlleleBeeSpecies) species, i);
				int color = ((ItemGE) ForestryItem.beeDroneGE.item()).getColourFromSpecies(species, i);

				float colorR = (color >> 16 & 255) / 255.0F;
				float colorG = (color >> 8 & 255) / 255.0F;
				float colorB = (color & 255) / 255.0F;

				GL11.glColor4f(colorR, colorG, colorB, 1.0F);
				// drawTexturedModalRect(startX + xPos, startY + yPos, iconIndex % 16 * 16, iconIndex / 16 * 16, 16, 16);
				manager.gui.drawTexturedModelRectFromIcon(startX + xPos, startY + yPos, icon, 16, 16);

			}
			GL11.glEnable(GL11.GL_LIGHTING);

		}

		@Override
		protected String getLegacyTooltip(EntityPlayer player) {
			IAlleleSpecies species = logic.getSpeciesFilter(orientation, pattern, allele);
			if (species != null)
				return species.getName();
			else
				return null;
		}

		@Override
		public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
			IAlleleSpecies change = null;
			if (mouseButton == 1)
				change = null;
			else if (getSpecies() == null) {

				Iterator it = AlleleManager.alleleRegistry.getRegisteredAlleles().entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, IAllele> entry = (Entry<String, IAllele>) it.next();
					if (!(entry.getValue() instanceof IAlleleBeeSpecies))
						continue;

					change = (IAlleleBeeSpecies) entry.getValue();
					break;
				}

			} else {

				Iterator it = AlleleManager.alleleRegistry.getRegisteredAlleles().entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, IAllele> entry = (Entry<String, IAllele>) it.next();
					if (!(entry.getValue() instanceof IAlleleBeeSpecies))
						continue;

					IAlleleBeeSpecies species = (IAlleleBeeSpecies) entry.getValue();
					if (!species.getUID().equals(getSpecies().getUID()))
						continue;

					while (it.hasNext()) {
						Entry<String, IAllele> entry2 = (Entry<String, IAllele>) it.next();
						if (!(entry2.getValue() instanceof IAlleleBeeSpecies))
							continue;

						IAlleleBeeSpecies next = (IAlleleBeeSpecies) entry2.getValue();
						if (!Config.isDebug && next.isSecret() && !tracker.isDiscovered(next))
							continue;

						change = next;
						break;
					}

					break;
				}
			}
			pipeLogic.setSpeciesFilter(orientation, pattern, allele, change);
		}
	}

	PipeLogicPropolis pipeLogic;

	public GuiPropolisPipe(EntityPlayer player, PipeItemsPropolis pipe) {
		super(Defaults.TEXTURE_PATH_GUI + "/analyzer.png", new ContainerPropolisPipe(player.inventory, pipe));

		pipeLogic = pipe.pipeLogic;
		// Request filter set update if on client
		if (!Proxies.common.isSimulating(pipe.getWorld()))
			pipeLogic.requestFilterSet();

		xSize = 175;
		ySize = 225;

		for (int i = 0; i < 6; i++)
			widgetManager.add(new TypeFilterSlot(8, 18 + i * 18, ForgeDirection.values()[i], pipeLogic));

		IApiaristTracker tracker = PluginApiculture.beeInterface.getBreedingTracker(player.worldObj, player.getGameProfile());
		for (int i = 0; i < 6; i++)
			for (int j = 0; j < 3; j++)
				for (int k = 0; k < 2; k++)
					widgetManager.add(new SpeciesFilterSlot(tracker, 44 + j * 45 + k * 18, 18 + i * 18, ForgeDirection.values()[i], j, k, pipeLogic));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		fontRendererObj.drawString("Apiarist's Pipe", 56, 6, 0x303030);
	}
}

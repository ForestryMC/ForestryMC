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

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import org.lwjgl.opengl.GL11;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IApiaristTracker;
import forestry.api.core.sprite.ISprite;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IIndividual;
import forestry.apiculture.genetics.BeeBranchDefinition;
import forestry.apiculture.genetics.BeeDefinition;
import forestry.apiculture.items.ItemBeeGE;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.genetics.ItemGE;
import forestry.core.gui.GuiForestry;
import forestry.core.gui.widgets.Widget;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;
import forestry.pipes.EnumFilterType;
import forestry.pipes.PipeItemsPropolis;
import forestry.pipes.PipeLogicPropolis;
import forestry.plugins.PluginAgriCraft;
import forestry.plugins.PluginApiculture;

/**
 * GuiScreen for propolis pipes.
 *
 * @author SirSengir
 */
public class GuiPropolisPipe extends GuiForestry<ContainerPropolisPipe, IInventory> {

	class TypeFilterSlot extends Widget {

		private final EnumFacing orientation;
		private final PipeLogicPropolis logic;

		public TypeFilterSlot(int x, int y, EnumFacing orientation, PipeLogicPropolis logic) {
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
			ISprite icon = null;
			if (type != null) {
				icon = type.getIcon();
			}
			if (icon == null) {
				return;
			}

			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0F);
			Proxies.common.bindTexture();
			drawTexturedModelRect(startX + xPos, startY + yPos, icon, 16, 16);

		}

		@Override
		protected String getLegacyTooltip(EntityPlayer player) {
			EnumFilterType type = logic.getTypeFilter(orientation);
			return StringUtil.localize("gui.pipe.filter." + type.toString().toLowerCase(Locale.ENGLISH));
		}

		@Override
		public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
			EnumFilterType change;
			if (mouseButton == 1) {
				change = EnumFilterType.CLOSED;
			} else if (getType().ordinal() < EnumFilterType.values().length - 1) {
				change = EnumFilterType.values()[getType().ordinal() + 1];
			} else {
				change = EnumFilterType.CLOSED;
			}
			pipeLogic.setTypeFilter(orientation, change);

		}

	}

	class SpeciesFilterSlot extends Widget {

		private final IApiaristTracker tracker;
		private final EnumFacing orientation;
		private final PipeLogicPropolis logic;
		private final int pattern;
		private final int allele;

		public SpeciesFilterSlot(IApiaristTracker tracker, int x, int y, EnumFacing orientation, int pattern, int allele, PipeLogicPropolis logic) {
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
			if (!isDefined()) {
				return;
			}

			IAlleleSpecies species = logic.getSpeciesFilter(orientation, pattern, allele);
			GL11.glDisable(GL11.GL_LIGHTING);

			/*for (int i = 0; i < 3; ++i) {

				ISprite icon = ((ItemBeeGE) ForestryItem.beeDroneGE.item()).getIconFromSpecies((IAlleleBeeSpecies) species, i);
				int color = ((ItemGE) ForestryItem.beeDroneGE.item()).getColourFromSpecies(species, i);

				float colorR = (color >> 16 & 255) / 255.0F;
				float colorG = (color >> 8 & 255) / 255.0F;
				float colorB = (color & 255) / 255.0F;

				GL11.glColor4f(colorR, colorG, colorB, 1.0F);
				// drawTexturedModalRect(startX + xPos, startY + yPos, iconIndex % 16 * 16, iconIndex / 16 * 16, 16, 16);
				manager.gui.drawTexturedModelRect(startX + xPos, startY + yPos, icon, 16, 16);

			}*/
			for(IIndividual individual : BeeManager.beeRoot.getIndividualTemplates())
			{
				if(individual.getGenome().getPrimary() == species)
				{
					NBTTagCompound nbttagcompound = new NBTTagCompound();
					ItemStack someStack = new ItemStack(ForestryItem.beeDroneGE.item());
					individual.writeToNBT(nbttagcompound);
					someStack.setTagCompound(nbttagcompound);
					Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(someStack, startX + xPos, startY + yPos);
					break;
				}
			}

		}

		@Override
		protected String getLegacyTooltip(EntityPlayer player) {
			IAlleleSpecies species = logic.getSpeciesFilter(orientation, pattern, allele);
			if (species != null) {
				return species.getName();
			} else {
				return null;
			}
		}

		@Override
		public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
			IAlleleSpecies change = null;
			if (mouseButton == 1) {
				change = null;
			} else if (getSpecies() == null) {

				for (Entry<String, IAllele> entry : AlleleManager.alleleRegistry.getRegisteredAlleles().entrySet()) {
					if (!(entry.getValue() instanceof IAlleleBeeSpecies)) {
						continue;
					}

					change = (IAlleleBeeSpecies) entry.getValue();
					break;
				}

			} else {

				Iterator<Entry<String, IAllele>> it = AlleleManager.alleleRegistry.getRegisteredAlleles().entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, IAllele> entry = it.next();
					if (!(entry.getValue() instanceof IAlleleBeeSpecies)) {
						continue;
					}

					IAlleleBeeSpecies species = (IAlleleBeeSpecies) entry.getValue();
					if (!species.getUID().equals(getSpecies().getUID())) {
						continue;
					}

					while (it.hasNext()) {
						Entry<String, IAllele> entry2 = it.next();
						if (!(entry2.getValue() instanceof IAlleleBeeSpecies)) {
							continue;
						}

						IAlleleBeeSpecies next = (IAlleleBeeSpecies) entry2.getValue();
						if (!Config.isDebug && next.isSecret() && !tracker.isDiscovered(next)) {
							continue;
						}

						change = next;
						break;
					}

					break;
				}
			}
			pipeLogic.setSpeciesFilter(orientation, pattern, allele, change);
		}
	}

	private final PipeLogicPropolis pipeLogic;

	public GuiPropolisPipe(EntityPlayer player, PipeItemsPropolis pipe) {
		super(Defaults.TEXTURE_PATH_GUI + "/analyzer.png", new ContainerPropolisPipe(player.inventory, pipe), null);

		pipeLogic = pipe.pipeLogic;
		// Request filter set update if on client
		if (!Proxies.common.isSimulating(pipe.getWorld())) {
			pipeLogic.requestFilterSet();
		}

		xSize = 175;
		ySize = 225;

		for (int i = 0; i < 6; i++) {
			widgetManager.add(new TypeFilterSlot(8, 18 + i * 18, EnumFacing.values()[i], pipeLogic));
		}

		IApiaristTracker tracker = BeeManager.beeRoot.getBreedingTracker(player.worldObj, player.getGameProfile());
		for (int i = 0; i < 6; i++) {
			for (int pattern = 0; pattern < 3; pattern++) {
				for (int allele = 0; allele < 2; allele++) {
					int x = 44 + pattern * 45 + allele * 18;
					int y = 18 + i * 18;
					widgetManager.add(new SpeciesFilterSlot(tracker, x, y, EnumFacing.values()[i], pattern, allele, pipeLogic));
				}
			}
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		fontRendererObj.drawString("Apiarist's Pipe", 56, 6, 0x303030);
	}
}

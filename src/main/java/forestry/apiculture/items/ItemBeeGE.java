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
package forestry.apiculture.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBee;
import forestry.api.core.Tabs;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.apiculture.genetics.BeeGenome;
import forestry.core.config.Config;
import forestry.core.genetics.ItemGE;
import forestry.core.utils.StringUtil;

public class ItemBeeGE extends ItemGE {

	private final EnumBeeType type;

	public ItemBeeGE(EnumBeeType type) {
		super(Tabs.tabApiculture);
		this.type = type;
		if (type != EnumBeeType.DRONE) {
			setMaxStackSize(1);
		}
	}

	@Override
	public IBee getIndividual(ItemStack itemstack) {
		return BeeManager.beeRoot.getMember(itemstack);
	}

	@Override
	protected IAlleleSpecies getSpecies(ItemStack itemStack) {
		return BeeGenome.getSpecies(itemStack);
	}

	@Override
	protected int getDefaultPrimaryColour() {
		return 0xffffff;
	}

	@Override
	protected int getDefaultSecondaryColour() {
		return 0xffdc16;
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {

		if (itemstack.getTagCompound() == null) {
			return super.getItemStackDisplayName(itemstack);
		}

		IBee individual = BeeManager.beeRoot.getMember(itemstack);
		String customBeeKey = "bees.custom." + type.getName() + "." + individual.getGenome().getPrimary().getUnlocalizedName().replace("bees.species.", "");
		if (StringUtil.canTranslate(customBeeKey)) {
			return StringUtil.localize(customBeeKey);
		}
		String beeGrammar = StringUtil.localize("bees.grammar." + type.getName());
		String beeSpecies = individual.getDisplayName();
		String beeType = StringUtil.localize("bees.grammar." + type.getName() + ".type");
		return beeGrammar.replaceAll("%SPECIES", beeSpecies).replaceAll("%TYPE", beeType);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean flag) {
		if (!itemstack.hasTagCompound()) {
			return;
		}

		if (type != EnumBeeType.DRONE) {
			IBee individual = BeeManager.beeRoot.getMember(itemstack);
			if (individual.isNatural()) {
				list.add(EnumChatFormatting.YELLOW + EnumChatFormatting.ITALIC.toString() + StringUtil.localize("bees.stock.pristine"));
			} else {
				list.add(EnumChatFormatting.YELLOW + StringUtil.localize("bees.stock.ignoble"));
			}
		}

		super.addInformation(itemstack, player, list, flag);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List itemList) {
		addCreativeItems(itemList, true);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public void addCreativeItems(List itemList, boolean hideSecrets) {
		for (IBee bee : BeeManager.beeRoot.getIndividualTemplates()) {
			// Don't show secret bees unless ordered to.
			if (hideSecrets && bee.isSecret() && !Config.isDebug) {
				continue;
			}

			ItemStack beeStack = BeeManager.beeRoot.getMemberStack(bee, type.ordinal());
			if (beeStack != null) {
				itemList.add(beeStack);
			}
		}
	}

	/* RENDERING */
	@Override
	public int getColorFromItemStack(ItemStack itemstack, int renderPass) {
		if (!itemstack.hasTagCompound()) {
			return super.getColorFromItemStack(itemstack, renderPass);
		}

		return getColourFromSpecies(BeeGenome.getSpecies(itemstack), renderPass);
	}

	@Override
	public int getColourFromSpecies(IAlleleSpecies species, int renderPass) {

		if (species instanceof IAlleleBeeSpecies) {
			return species.getIconColour(renderPass);
		} else {
			return 0xffffff;
		}

	}

	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	public int getRenderPasses(int metadata) {
		return 3;
	}

	/* ICONS */
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
			if (allele instanceof IAlleleBeeSpecies) {
				((IAlleleBeeSpecies) allele).getIconProvider().registerIcons(register);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack itemstack, int renderPass) {
		return getIconFromSpecies(BeeGenome.getSpecies(itemstack), renderPass);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconIndex(ItemStack stack) {
		return getIcon(stack, 0);
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIconFromSpecies(IAlleleBeeSpecies species, int renderPass) {
		if (species == null) {
			species = (IAlleleBeeSpecies) BeeManager.beeRoot.getDefaultTemplate()[EnumBeeChromosome.SPECIES.ordinal()];
		}

		return species.getIcon(type, renderPass);
	}
}

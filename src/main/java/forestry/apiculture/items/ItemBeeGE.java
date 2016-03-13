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

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBee;
import forestry.api.core.IModelManager;
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

	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List<String> list, boolean flag) {
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

	@Override
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List<ItemStack> itemList) {
		addCreativeItems(itemList, true);
	}

	public void addCreativeItems(List<ItemStack> itemList, boolean hideSecrets) {
		for (IBee bee : BeeManager.beeRoot.getIndividualTemplates()) {
			// Don't show secret bees unless ordered to.
			if (hideSecrets && bee.isSecret() && !Config.isDebug) {
				continue;
			}

			ItemStack beeStack = BeeManager.beeRoot.getMemberStack(bee, type);
			if (beeStack != null) {
				itemList.add(beeStack);
			}
		}
	}

	/* MODELS */
	@SideOnly(Side.CLIENT)
	@Override
	public int getColorFromItemStack(ItemStack itemstack, int renderPass) {
		if (!itemstack.hasTagCompound()) {
			if (renderPass == 1) {
				return 0xffdc16;
			} else {
				return 0xffffff;
			}
		}

		return getColourFromSpecies(BeeGenome.getSpecies(itemstack), renderPass);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getColourFromSpecies(IAlleleSpecies species, int renderPass) {

		if (species instanceof IAlleleBeeSpecies) {
			return species.getSpriteColour(renderPass);
		} else {
			return 0xffffff;
		}

	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
			if (allele instanceof IAlleleBeeSpecies) {
				((IAlleleBeeSpecies) allele).registerModels(item, manager);
			}
		}
		manager.registerItemModel(item, new BeeMeshDefinition());
	}

	private class BeeMeshDefinition implements ItemMeshDefinition {

		@Override
		public ModelResourceLocation getModelLocation(ItemStack stack) {
			IAlleleBeeSpecies species = (IAlleleBeeSpecies) getSpecies(stack);
			if (species == null) {
				species = (IAlleleBeeSpecies) BeeManager.beeRoot.getDefaultTemplate()[EnumBeeChromosome.SPECIES.ordinal()];
			}
			return species.getModel(type);
		}

	}

	public final EnumBeeType getType() {
		return type;
	}
}

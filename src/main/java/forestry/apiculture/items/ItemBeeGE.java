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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBee;
import forestry.api.core.IModelManager;
import forestry.api.core.Tabs;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.apiculture.genetics.BeeDefinition;
import forestry.apiculture.genetics.BeeGenome;
import forestry.apiculture.genetics.DefaultBeeModelProvider;
import forestry.core.config.Config;
import forestry.core.genetics.ItemGE;
import forestry.core.items.IColoredItem;
import forestry.core.utils.Translator;

public class ItemBeeGE extends ItemGE implements IColoredItem {

	private final EnumBeeType type;

	public ItemBeeGE(EnumBeeType type) {
		super(Tabs.tabApiculture);
		this.type = type;
		if (type != EnumBeeType.DRONE) {
			setMaxStackSize(1);
		}
	}

	@Nonnull
	@Override
	public IBee getIndividual(ItemStack itemstack) {
		IBee individual = BeeManager.beeRoot.getMember(itemstack);
		if (individual == null) {
			individual = BeeDefinition.FOREST.getIndividual();
		}
		return individual;
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

		IBee individual = getIndividual(itemstack);
		String customBeeKey = "for.bees.custom." + type.getName() + "." + individual.getGenome().getPrimary().getUnlocalizedName().replace("bees.species.", "");
		if (Translator.canTranslateToLocal(customBeeKey)) {
			return Translator.translateToLocal(customBeeKey);
		}
		String beeGrammar = Translator.translateToLocal("for.bees.grammar." + type.getName());
		String beeSpecies = individual.getDisplayName();
		String beeType = Translator.translateToLocal("for.bees.grammar." + type.getName() + ".type");
		return beeGrammar.replaceAll("%SPECIES", beeSpecies).replaceAll("%TYPE", beeType);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, @Nullable World world, List<String> list, ITooltipFlag flag) {
		if (itemstack.getTagCompound() == null) {
			return;
		}

		if (type != EnumBeeType.DRONE) {
			IBee individual = getIndividual(itemstack);
			if (individual.isNatural()) {
				list.add(TextFormatting.YELLOW + TextFormatting.ITALIC.toString() + Translator.translateToLocal("for.bees.stock.pristine"));
			} else {
				list.add(TextFormatting.YELLOW + Translator.translateToLocal("for.bees.stock.ignoble"));
			}
		}

		super.addInformation(itemstack, world, list, flag);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (this.isInCreativeTab(tab)) {
			addCreativeItems(subItems, true);
		}
	}

	public void addCreativeItems(NonNullList<ItemStack> subItems, boolean hideSecrets) {
		for (IBee bee : BeeManager.beeRoot.getIndividualTemplates()) {
			// Don't show secret bees unless ordered to.
			if (hideSecrets && bee.isSecret() && !Config.isDebug) {
				continue;
			}

			ItemStack beeStack = BeeManager.beeRoot.getMemberStack(bee, type);
			if (!beeStack.isEmpty()) {
				subItems.add(beeStack);
			}
		}
	}

	@Override
	public int getColorFromItemstack(ItemStack itemstack, int tintIndex) {
		if (itemstack.getTagCompound() == null) {
			if (tintIndex == 1) {
				return 0xffdc16;
			} else {
				return 0xffffff;
			}
		}

		IAlleleBeeSpecies species = BeeGenome.getSpecies(itemstack);
		return species.getSpriteColour(tintIndex);
	}

	/* MODELS */
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

	@SideOnly(Side.CLIENT)
	private class BeeMeshDefinition implements ItemMeshDefinition {
		@Override
		public ModelResourceLocation getModelLocation(ItemStack stack) {
			if (!stack.hasTagCompound()) { // villager trade wildcard bees
				return DefaultBeeModelProvider.instance.getModel(type);
			}
			IAlleleBeeSpecies species = (IAlleleBeeSpecies) getSpecies(stack);
			return species.getModel(type);
		}
	}

	public final EnumBeeType getType() {
		return type;
	}
}

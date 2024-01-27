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

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import forestry.api.apiculture.genetics.BeeChromosomes;
import forestry.api.apiculture.genetics.EnumBeeType;
import forestry.api.apiculture.genetics.IAlleleBeeSpecies;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.apiculture.genetics.IBeeRoot;
import forestry.api.core.ItemGroups;
import forestry.apiculture.genetics.BeeHelper;
import forestry.core.config.Config;
import forestry.core.genetics.ItemGE;
import forestry.core.items.definitions.IColoredItem;

import genetics.api.GeneticHelper;

public class ItemBeeGE extends ItemGE implements IColoredItem {

	private final EnumBeeType type;

	public ItemBeeGE(EnumBeeType type) {
		super(type != EnumBeeType.DRONE ? new Item.Properties().tab(ItemGroups.tabApiculture).durability(1) : new Item.Properties().tab(ItemGroups.tabApiculture));
		this.type = type;
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return GeneticHelper.createOrganism(stack, type, BeeHelper.getRoot().getDefinition());
	}

	@Override
	protected IAlleleBeeSpecies getSpecies(ItemStack itemStack) {
		return GeneticHelper.getOrganism(itemStack).getAllele(BeeChromosomes.SPECIES, true);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack itemstack, @Nullable Level world, List<Component> list, TooltipFlag flag) {
		if (!itemstack.hasTag()) {
			return;
		}

		if (type != EnumBeeType.DRONE) {
			Optional<IBee> optionalIndividual = GeneticHelper.getIndividual(itemstack);
			if (!optionalIndividual.isPresent()) {
				return;
			}

			IBee individual = optionalIndividual.get();
			if (individual.isNatural()) {
				list.add(Component.translatable("for.bees.stock.pristine").withStyle(ChatFormatting.YELLOW, ChatFormatting.ITALIC));
			} else {
				list.add(Component.translatable("for.bees.stock.ignoble").withStyle(ChatFormatting.YELLOW));
			}
		}

		super.appendHoverText(itemstack, world, list, flag);
	}

	@Override
	public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> subItems) {
		if (this.allowedIn(tab)) {
			addCreativeItems(subItems, true);
		}
	}

	public void addCreativeItems(NonNullList<ItemStack> subItems, boolean hideSecrets) {
		//so need to adjust init sequence
		IBeeRoot root = BeeHelper.getRoot();
		for (IBee bee : root.getIndividualTemplates()) {
			// Don't show secret bees unless ordered to.
			if (hideSecrets && bee.isSecret() && !Config.isDebug) {
				continue;
			}
			ItemStack stack = new ItemStack(this);
			GeneticHelper.setIndividual(stack, bee);
			subItems.add(stack);
		}
	}

	@Override
	public int getColorFromItemStack(ItemStack itemstack, int tintIndex) {
		if (!itemstack.hasTag()) {
			if (tintIndex == 1) {
				return 0xffdc16;
			} else {
				return 0xffffff;
			}
		}

		IAlleleBeeSpecies species = getSpecies(itemstack);
		return species.getSpriteColour(tintIndex);
	}

	public final EnumBeeType getType() {
		return type;
	}
}

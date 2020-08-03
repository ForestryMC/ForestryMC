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

import forestry.api.apiculture.genetics.*;
import forestry.api.core.ItemGroups;
import forestry.apiculture.genetics.BeeHelper;
import forestry.core.config.Config;
import forestry.core.genetics.ItemGE;
import forestry.core.items.IColoredItem;
import forestry.core.utils.ResourceUtil;
import genetics.api.GeneticHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ItemBeeGE extends ItemGE implements IColoredItem {

    private final EnumBeeType type;

    public ItemBeeGE(EnumBeeType type) {
        super(type != EnumBeeType.DRONE ? new Item.Properties().group(ItemGroups.tabApiculture).maxDamage(1) : new Item.Properties().group(ItemGroups.tabApiculture));
        this.type = type;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return GeneticHelper.createOrganism(stack, type, BeeHelper.getRoot().getDefinition());
    }

    @Override
    protected IAlleleBeeSpecies getSpecies(ItemStack itemStack) {
        return GeneticHelper.getOrganism(itemStack).getAllele(BeeChromosomes.SPECIES, true);
    }

    //TODO - pretty sure this is still translating on the server atm
    @Override
    public ITextComponent getDisplayName(ItemStack itemStack) {
        if (GeneticHelper.getOrganism(itemStack).isEmpty()) {
            return super.getDisplayName(itemStack);
        }
        Optional<IBee> optionalIndividual = GeneticHelper.getIndividual(itemStack);
        if (!optionalIndividual.isPresent()) {
            return super.getDisplayName(itemStack);
        }

        IBee individual = optionalIndividual.get();
        String customBeeKey = "for.bees.custom." + type.getName() + "." + individual.getGenome().getPrimary().getLocalisationKey().replace("bees.species.", "");

        return ResourceUtil.tryTranslate(customBeeKey, () -> {
            ITextComponent beeSpecies = individual.getGenome().getPrimary().getDisplayName();
            ITextComponent beeType = new TranslationTextComponent("for.bees.grammar." + type.getName() + ".type");
            return new TranslationTextComponent("for.bees.grammar." + type.getName(), beeSpecies, beeType);
        });
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack itemstack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
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
                list.add(new TranslationTextComponent("for.bees.stock.pristine").mergeStyle(TextFormatting.YELLOW, TextFormatting.ITALIC));
            } else {
                list.add(new TranslationTextComponent("for.bees.stock.ignoble").mergeStyle(TextFormatting.YELLOW));
            }
        }

        super.addInformation(itemstack, world, list, flag);
    }

    @Override
    public void fillItemGroup(ItemGroup tab, NonNullList<ItemStack> subItems) {
        if (this.isInGroup(tab)) {
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

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
package forestry.core.genetics;

import com.mojang.authlib.GameProfile;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IForestrySpeciesRoot;
import forestry.api.genetics.alleles.IAlleleForestrySpecies;
import forestry.core.genetics.mutations.EnumMutateChance;
import forestry.core.items.ItemForestry;
import forestry.core.utils.NetworkUtil;
import genetics.api.GeneticsAPI;
import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleSpecies;
import genetics.api.individual.IIndividual;
import genetics.api.mutation.IMutation;
import genetics.api.mutation.IMutationContainer;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.IRootDefinition;
import genetics.api.root.components.ComponentKeys;
import genetics.utils.AlleleUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ItemResearchNote extends ItemForestry {

    private static final String NBT_ALLELE_FIRST = "AL0";
    private static final String NBT_ALLELE_SECOND = "AL1";
    private static final String NBT_ALLELE_RESULT = "RST";
    private static final String NBT_ROOT = "ROT";

    private static final String NBT_RESEARCHER = "RES";
    private static final String NBT_TYPE = "TYP";
    private static final String NBT_INNER = "INN";

    public enum EnumNoteType {
        NONE, MUTATION, SPECIES;

        public static final EnumNoteType[] VALUES = values();

        @Nullable
        private static IMutation getEncodedMutation(IIndividualRoot<IIndividual> root, CompoundNBT compound) {
            IAllele allele0 = AlleleUtils.getAlleleOrNull(compound.getString(NBT_ALLELE_FIRST));
            IAllele allele1 = AlleleUtils.getAlleleOrNull(compound.getString(NBT_ALLELE_SECOND));
            if (allele0 == null || allele1 == null) {
                return null;
            }

            IAllele result = null;
            if (compound.contains(NBT_ALLELE_RESULT)) {
                result = AlleleUtils.getAlleleOrNull(compound.getString(NBT_ALLELE_RESULT));
            }

            IMutation encoded = null;
            IMutationContainer<IIndividual, IMutation> container = root.getComponent(ComponentKeys.MUTATIONS);
            for (IMutation mutation : container.getCombinations(allele0)) {
                if (mutation.isPartner(allele1)) {
                    if (result == null
                            || mutation.getTemplate()[0].getRegistryName().equals(result.getRegistryName())) {
                        encoded = mutation;
                        break;
                    }
                }
            }

            return encoded;
        }

        public List<ITextComponent> getTooltip(CompoundNBT compound) {
            List<ITextComponent> tooltips = new ArrayList<>();

            if (this == NONE) {
                return tooltips;
            }

            if (this == MUTATION) {
                IRootDefinition<IIndividualRoot<IIndividual>> definition = GeneticsAPI.apiInstance.getRoot(compound.getString(
                        "ROT"));
                if (!definition.isPresent()) {
                    return tooltips;
                }
                IIndividualRoot<IIndividual> root = definition.get();

                IMutation encoded = getEncodedMutation(root, compound);
                if (encoded == null) {
                    return tooltips;
                }

                ITextComponent species1 = encoded.getFirstParent().getDisplayName();
                ITextComponent species2 = encoded.getSecondParent().getDisplayName();
                String mutationChanceKey = EnumMutateChance.rateChance(encoded.getBaseChance()).toString();
                String mutationChance = new TranslationTextComponent("for.researchNote.chance." + mutationChanceKey).getString();
                ITextComponent speciesResult = encoded.getResultingSpecies().getDisplayName();

                tooltips.add(new TranslationTextComponent("for.researchNote.discovery.0"));
                tooltips.add(new TranslationTextComponent("for.researchNote.discovery.1", species1, species2));
                tooltips.add(new TranslationTextComponent("for.researchNote.discovery.2", mutationChance));
                tooltips.add(new TranslationTextComponent("for.researchNote.discovery.3", speciesResult));

                if (!encoded.getSpecialConditions().isEmpty()) {
                    for (ITextComponent line : encoded.getSpecialConditions()) {
                        tooltips.add(((IFormattableTextComponent) line).mergeStyle(TextFormatting.GOLD));
                    }
                }
            } else if (this == SPECIES) {
                IAlleleForestrySpecies alleleFirst = AlleleUtils.getAlleleOrNull(compound.getString(NBT_ALLELE_FIRST));
                if (alleleFirst == null) {
                    return tooltips;
                }
                IRootDefinition<IIndividualRoot<IIndividual>> definition = GeneticsAPI.apiInstance.getRoot(compound.getString(
                        NBT_ROOT));
                definition.ifPresent(root -> {
                    tooltips.add(new TranslationTextComponent("researchNote.discovered.0"));
                    tooltips.add(new TranslationTextComponent(
                            "for.researchNote.discovered.1",
                            alleleFirst.getDisplayName(),
                            alleleFirst.getBinomial()
                    ));
                });
            }

            return tooltips;
        }

        public boolean registerResults(World world, PlayerEntity player, CompoundNBT compound) {
            if (this == NONE) {
                return false;
            }

            if (this == MUTATION) {
                IRootDefinition<IIndividualRoot<IIndividual>> definition = GeneticsAPI.apiInstance.getRoot(compound.getString(
                        "ROT"));
                if (!definition.isPresent()) {
                    return false;
                }
                IIndividualRoot<IIndividual> root = definition.get();

                IMutation encoded = getEncodedMutation(root, compound);
                if (encoded == null) {
                    return false;
                }

                IBreedingTracker tracker = ((IForestrySpeciesRoot) encoded.getRoot()).getBreedingTracker(
                        world,
                        player.getGameProfile()
                );
                if (tracker.isResearched(encoded)) {
                    player.sendMessage(new TranslationTextComponent("for.chat.cannotmemorizeagain"), Util.DUMMY_UUID);
                    return false;
                }

                IAlleleSpecies speciesFirst = encoded.getFirstParent();
                IAlleleSpecies speciesSecond = encoded.getSecondParent();
                IAlleleSpecies speciesResult = encoded.getResultingSpecies();

                tracker.registerSpecies(speciesFirst);
                tracker.registerSpecies(speciesSecond);
                tracker.registerSpecies(speciesResult);

                tracker.researchMutation(encoded);
                player.sendMessage(new TranslationTextComponent("for.chat.memorizednote"), Util.DUMMY_UUID);

                player.sendMessage(new TranslationTextComponent(
                        "for.chat.memorizednote2",
                        ((IFormattableTextComponent) speciesFirst.getDisplayName()).mergeStyle(TextFormatting.GRAY),
                        ((IFormattableTextComponent) speciesSecond.getDisplayName()).mergeStyle(TextFormatting.GRAY),
                        ((IFormattableTextComponent) speciesResult.getDisplayName()).mergeStyle(TextFormatting.GREEN)
                ), Util.DUMMY_UUID);

                return true;
            }

            return false;

        }

        public static ResearchNote createMutationNote(GameProfile researcher, IMutation mutation) {
            CompoundNBT compound = new CompoundNBT();
            compound.putString(NBT_ROOT, mutation.getRoot().getUID());
            compound.putString(NBT_ALLELE_FIRST, mutation.getFirstParent().getRegistryName().toString());
            compound.putString(NBT_ALLELE_SECOND, mutation.getSecondParent().getRegistryName().toString());
            compound.putString(NBT_ALLELE_RESULT, mutation.getResultingSpecies().getRegistryName().toString());
            return new ResearchNote(researcher, MUTATION, compound);
        }

        public static ItemStack createMutationNoteStack(Item item, GameProfile researcher, IMutation mutation) {
            ResearchNote note = createMutationNote(researcher, mutation);
            CompoundNBT compound = new CompoundNBT();
            note.writeToNBT(compound);
            ItemStack created = new ItemStack(item);
            created.setTag(compound);
            return created;
        }

        public static ResearchNote createSpeciesNote(GameProfile researcher, IAlleleForestrySpecies species) {
            CompoundNBT compound = new CompoundNBT();
            compound.putString(NBT_ROOT, species.getRoot().getUID());
            compound.putString(NBT_ALLELE_FIRST, species.getRegistryName().toString());
            return new ResearchNote(researcher, SPECIES, compound);
        }

        public static ItemStack createSpeciesNoteStack(
                Item item,
                GameProfile researcher,
                IAlleleForestrySpecies species
        ) {
            ResearchNote note = createSpeciesNote(researcher, species);
            CompoundNBT compound = new CompoundNBT();
            note.writeToNBT(compound);
            ItemStack created = new ItemStack(item);
            created.setTag(compound);
            return created;
        }

    }

    public static class ResearchNote {
        @Nullable
        private final GameProfile researcher;
        private final EnumNoteType type;
        private final CompoundNBT inner;

        public ResearchNote(GameProfile researcher, EnumNoteType type, CompoundNBT inner) {
            this.researcher = researcher;
            this.type = type;
            this.inner = inner;
        }

        public ResearchNote(@Nullable CompoundNBT compound) {
            if (compound != null) {
                if (compound.contains(NBT_RESEARCHER)) {
                    this.researcher = NBTUtil.readGameProfile(compound.getCompound(NBT_RESEARCHER));
                } else {
                    this.researcher = null;
                }
                this.type = EnumNoteType.VALUES[compound.getByte(NBT_TYPE)];
                this.inner = compound.getCompound(NBT_INNER);
            } else {
                this.type = EnumNoteType.NONE;
                this.researcher = null;
                this.inner = new CompoundNBT();
            }
        }

        public CompoundNBT writeToNBT(CompoundNBT compound) {
            if (this.researcher != null) {
                CompoundNBT nbt = new CompoundNBT();
                NBTUtil.writeGameProfile(nbt, researcher);
                compound.put(NBT_RESEARCHER, nbt);
            }
            compound.putByte(NBT_TYPE, (byte) type.ordinal());
            compound.put(NBT_INNER, inner);
            return compound;
        }

        public void addTooltip(List<ITextComponent> list) {
            List<ITextComponent> tooltips = type.getTooltip(inner);
            if (tooltips.isEmpty()) {
                list.add(new TranslationTextComponent("for.researchNote.error.0").mergeStyle(
                        TextFormatting.RED,
                        TextFormatting.ITALIC
                ));
                list.add(new TranslationTextComponent("for.researchNote.error.1"));
                return;
            }

            list.addAll(tooltips);
        }

        public boolean registerResults(World world, PlayerEntity player) {
            return type.registerResults(world, player, inner);
        }
    }

    public ItemResearchNote() {
        super((new Item.Properties()).group(null));
    }

    @Override
    public ITextComponent getDisplayName(ItemStack itemstack) {
        ResearchNote note = new ResearchNote(itemstack.getTag());
        String researcherName;
        if (note.researcher == null) {
            researcherName = "Sengir";
        } else {
            researcherName = note.researcher.getName();
        }
        return new TranslationTextComponent(getTranslationKey(itemstack), researcherName);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(
            ItemStack itemstack,
            @Nullable World world,
            List<ITextComponent> list,
            ITooltipFlag flag
    ) {
        super.addInformation(itemstack, world, list, flag);
        ResearchNote note = new ResearchNote(itemstack.getTag());
        note.addTooltip(list);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack heldItem = playerIn.getHeldItem(handIn);
        if (worldIn.isRemote) {
            return ActionResult.resultPass(heldItem);
        }

        ResearchNote note = new ResearchNote(heldItem.getTag());
        if (note.registerResults(worldIn, playerIn)) {
            playerIn.inventory.decrStackSize(playerIn.inventory.currentItem, 1);
            // Notify player that his inventory has changed.
            NetworkUtil.inventoryChangeNotify(playerIn, playerIn.openContainer);    //TODO not sure this is right
        }

        return ActionResult.resultSuccess(heldItem);
    }
}

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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.Util;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IForestrySpeciesRoot;
import forestry.api.genetics.alleles.IAlleleForestrySpecies;
import forestry.core.genetics.mutations.EnumMutateChance;
import forestry.core.items.ItemForestry;
import forestry.core.utils.NetworkUtil;
import forestry.core.utils.Translator;

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
		private static IMutation getEncodedMutation(IIndividualRoot<IIndividual> root, CompoundTag compound) {
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

		public List<Component> getTooltip(CompoundTag compound) {
			List<Component> tooltips = new ArrayList<>();

			if (this == NONE) {
				return tooltips;
			}

			if (this == MUTATION) {
				IRootDefinition<IIndividualRoot<IIndividual>> definition = GeneticsAPI.apiInstance.getRoot(compound.getString("ROT"));
				if (!definition.isPresent()) {
					return tooltips;
				}
				IIndividualRoot<IIndividual> root = definition.get();

				IMutation encoded = getEncodedMutation(root, compound);
				if (encoded == null) {
					return tooltips;
				}

				Component species1 = encoded.getFirstParent().getDisplayName();
				Component species2 = encoded.getSecondParent().getDisplayName();
				String mutationChanceKey = EnumMutateChance.rateChance(encoded.getBaseChance()).toString().toLowerCase(Locale.ENGLISH);
				String mutationChance = Translator.translateToLocal("for.researchNote.chance." + mutationChanceKey);
				Component speciesResult = encoded.getResultingSpecies().getDisplayName();

				tooltips.add(Component.translatable("for.researchNote.discovery.0"));
				tooltips.add(Component.translatable("for.researchNote.discovery.1", species1, species2));
				tooltips.add(Component.translatable("for.researchNote.discovery.2", mutationChance));
				tooltips.add(Component.translatable("for.researchNote.discovery.3", speciesResult));

				if (!encoded.getSpecialConditions().isEmpty()) {
					for (Component line : encoded.getSpecialConditions()) {
						tooltips.add(((MutableComponent) line).withStyle(ChatFormatting.GOLD));
					}
				}
			} else if (this == SPECIES) {
				IAlleleForestrySpecies alleleFirst = AlleleUtils.getAlleleOrNull(compound.getString(NBT_ALLELE_FIRST));
				if (alleleFirst == null) {
					return tooltips;
				}
				IRootDefinition<IIndividualRoot<IIndividual>> definition = GeneticsAPI.apiInstance.getRoot(compound.getString(NBT_ROOT));
				definition.ifPresent(root -> {
					tooltips.add(Component.translatable("researchNote.discovered.0"));
					tooltips.add(Component.translatable("for.researchNote.discovered.1", alleleFirst.getDisplayName(), alleleFirst.getBinomial()));
				});
			}

			return tooltips;
		}

		public boolean registerResults(Level world, Player player, CompoundTag compound) {
			if (this == NONE) {
				return false;
			}

			if (this == MUTATION) {
				IRootDefinition<IIndividualRoot<IIndividual>> definition = GeneticsAPI.apiInstance.getRoot(compound.getString("ROT"));
				if (!definition.isPresent()) {
					return false;
				}
				IIndividualRoot<IIndividual> root = definition.get();

				IMutation encoded = getEncodedMutation(root, compound);
				if (encoded == null) {
					return false;
				}

				IBreedingTracker tracker = ((IForestrySpeciesRoot) encoded.getRoot()).getBreedingTracker(world, player.getGameProfile());
				if (tracker.isResearched(encoded)) {
					player.sendMessage(Component.translatable("for.chat.cannotmemorizeagain"), Util.NIL_UUID);
					return false;
				}

				IAlleleSpecies speciesFirst = encoded.getFirstParent();
				IAlleleSpecies speciesSecond = encoded.getSecondParent();
				IAlleleSpecies speciesResult = encoded.getResultingSpecies();

				tracker.registerSpecies(speciesFirst);
				tracker.registerSpecies(speciesSecond);
				tracker.registerSpecies(speciesResult);

				tracker.researchMutation(encoded);
				player.sendMessage(Component.translatable("for.chat.memorizednote"), Util.NIL_UUID);

				player.sendMessage(Component.translatable("for.chat.memorizednote2",
						((MutableComponent) speciesFirst.getDisplayName()).withStyle(ChatFormatting.GRAY),
						((MutableComponent) speciesSecond.getDisplayName()).withStyle(ChatFormatting.GRAY),
						((MutableComponent) speciesResult.getDisplayName()).withStyle(ChatFormatting.GREEN)), Util.NIL_UUID);

				return true;
			}

			return false;

		}

		public static ResearchNote createMutationNote(GameProfile researcher, IMutation mutation) {
			CompoundTag compound = new CompoundTag();
			compound.putString(NBT_ROOT, mutation.getRoot().getUID());
			compound.putString(NBT_ALLELE_FIRST, mutation.getFirstParent().getRegistryName().toString());
			compound.putString(NBT_ALLELE_SECOND, mutation.getSecondParent().getRegistryName().toString());
			compound.putString(NBT_ALLELE_RESULT, mutation.getResultingSpecies().getRegistryName().toString());
			return new ResearchNote(researcher, MUTATION, compound);
		}

		public static ItemStack createMutationNoteStack(Item item, GameProfile researcher, IMutation mutation) {
			ResearchNote note = createMutationNote(researcher, mutation);
			CompoundTag compound = new CompoundTag();
			note.writeToNBT(compound);
			ItemStack created = new ItemStack(item);
			created.setTag(compound);
			return created;
		}

		public static ResearchNote createSpeciesNote(GameProfile researcher, IAlleleForestrySpecies species) {
			CompoundTag compound = new CompoundTag();
			compound.putString(NBT_ROOT, species.getRoot().getUID());
			compound.putString(NBT_ALLELE_FIRST, species.getRegistryName().toString());
			return new ResearchNote(researcher, SPECIES, compound);
		}

		public static ItemStack createSpeciesNoteStack(Item item, GameProfile researcher, IAlleleForestrySpecies species) {
			ResearchNote note = createSpeciesNote(researcher, species);
			CompoundTag compound = new CompoundTag();
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
		private final CompoundTag inner;

		public ResearchNote(GameProfile researcher, EnumNoteType type, CompoundTag inner) {
			this.researcher = researcher;
			this.type = type;
			this.inner = inner;
		}

		public ResearchNote(@Nullable CompoundTag compound) {
			if (compound != null) {
				if (compound.contains(NBT_RESEARCHER)) {
					this.researcher = NbtUtils.readGameProfile(compound.getCompound(NBT_RESEARCHER));
				} else {
					this.researcher = null;
				}
				this.type = EnumNoteType.VALUES[compound.getByte(NBT_TYPE)];
				this.inner = compound.getCompound(NBT_INNER);
			} else {
				this.type = EnumNoteType.NONE;
				this.researcher = null;
				this.inner = new CompoundTag();
			}
		}

		public CompoundTag writeToNBT(CompoundTag compound) {
			if (this.researcher != null) {
				CompoundTag nbt = new CompoundTag();
				NbtUtils.writeGameProfile(nbt, researcher);
				compound.put(NBT_RESEARCHER, nbt);
			}
			compound.putByte(NBT_TYPE, (byte) type.ordinal());
			compound.put(NBT_INNER, inner);
			return compound;
		}

		public void addTooltip(List<Component> list) {
			List<Component> tooltips = type.getTooltip(inner);
			if (tooltips.isEmpty()) {
				list.add(Component.translatable("for.researchNote.error.0").withStyle(ChatFormatting.RED, ChatFormatting.ITALIC));
				list.add(Component.translatable("for.researchNote.error.1"));
				return;
			}

			list.addAll(tooltips);
		}

		public boolean registerResults(Level world, Player player) {
			return type.registerResults(world, player, inner);
		}
	}

	public ItemResearchNote() {
		super((new Item.Properties()).tab(null));
	}

	@Override
	public Component getName(ItemStack itemstack) {
		ResearchNote note = new ResearchNote(itemstack.getTag());
		String researcherName;
		if (note.researcher == null) {
			researcherName = "Sengir";
		} else {
			researcherName = note.researcher.getName();
		}
		return Component.translatable(getDescriptionId(itemstack), researcherName);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack itemstack, @Nullable Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		ResearchNote note = new ResearchNote(itemstack.getTag());
		note.addTooltip(list);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		ItemStack heldItem = playerIn.getItemInHand(handIn);
		if (worldIn.isClientSide) {
			return InteractionResultHolder.pass(heldItem);
		}

		ResearchNote note = new ResearchNote(heldItem.getTag());
		if (note.registerResults(worldIn, playerIn)) {
			playerIn.getInventory().removeItem(playerIn.getInventory().selected, 1);
			// Notify player that his inventory has changed.
			NetworkUtil.inventoryChangeNotify(playerIn, playerIn.containerMenu);    //TODO not sure this is right
		}

		return InteractionResultHolder.success(heldItem);
	}
}

package forestry.core.genetics.research;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.mojang.authlib.GameProfile;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IMutation;
import forestry.core.genetics.mutations.Mutation;
import forestry.core.utils.PlayerUtil;

public enum EnumNoteType {
	MUTATION {
		@Nullable
		@Override
		public ResearchNote createResearchNote(@Nonnull GameProfile researcher, @Nonnull NBTTagCompound nbt) {
			NBTTagCompound mutationNbt = nbt.getCompoundTag(ResearchNoteMutation.MUTATION_NBT_KEY);
			if (mutationNbt == null) {
				return null;
			}
			IMutation<?> mutation = Mutation.create(mutationNbt);
			if (mutation == null) {
				return null;
			}
			return new ResearchNoteMutation<>(researcher, mutation);
		}
	},
	SPECIES {
		@Nullable
		@Override
		public ResearchNote createResearchNote(@Nonnull GameProfile researcher, @Nonnull NBTTagCompound nbt) {
			String speciesUid = nbt.getString(ResearchNoteSpecies.SPECIES_NBT_KEY);
			if (speciesUid == null) {
				return null;
			}
			IAllele allele = AlleleManager.alleleRegistry.getAllele(speciesUid);
			if (!(allele instanceof IAlleleSpecies)) {
				return null;
			}
			IAlleleSpecies<?> alleleSpecies = (IAlleleSpecies) allele;
			return new ResearchNoteSpecies<>(researcher, alleleSpecies);
		}
	};

	public static final EnumNoteType[] VALUES = values();

	@Nullable
	public ResearchNote createResearchNote(@Nonnull NBTTagCompound nbt) {
		NBTTagCompound researcherNbt = nbt.getCompoundTag(ResearchNote.RESEARCHER_NBT_KEY);
		if (researcherNbt == null) {
			return null;
		}
		GameProfile researcher = PlayerUtil.readGameProfileFromNBT(researcherNbt);
		if (researcher == null) {
			return null;
		}
		return createResearchNote(researcher, nbt);
	}

	@Nullable
	protected abstract ResearchNote createResearchNote(@Nonnull GameProfile researcher, @Nonnull NBTTagCompound nbt);

	public static ItemStack createMutationNoteStack(@Nonnull Item item, @Nonnull GameProfile researcher, @Nonnull IMutation<?> mutation) {
		ResearchNote note = new ResearchNoteMutation<>(researcher, mutation);
		NBTTagCompound compound = new NBTTagCompound();
		note.writeToNBT(compound);
		ItemStack created = new ItemStack(item);
		created.setTagCompound(compound);
		return created;
	}

	public static ItemStack createSpeciesNoteStack(@Nonnull Item item, @Nonnull GameProfile researcher, @Nonnull IAlleleSpecies<?> species) {
		ResearchNote note = new ResearchNoteSpecies<>(researcher, species);
		NBTTagCompound compound = new NBTTagCompound();
		note.writeToNBT(compound);
		ItemStack created = new ItemStack(item);
		created.setTagCompound(compound);
		return created;
	}

}

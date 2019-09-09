package genetics.individual;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.util.Constants;

import genetics.api.alleles.IAllele;
import genetics.api.individual.IChromosome;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IKaryotype;

import genetics.utils.SimpleByteBuf;

public enum SaveFormat {
	//Used before forge fires the FMLLoadCompleteEvent.
	UID {
		@Override
		public CompoundNBT writeTag(IChromosome[] chromosomes, IKaryotype karyotype, CompoundNBT tagCompound) {
			ListNBT tagList = new ListNBT();
			for (int i = 0; i < chromosomes.length; i++) {
				if (chromosomes[i] != null) {
					CompoundNBT chromosomeTag = new CompoundNBT();
					chromosomeTag.putByte(SLOT_TAG, (byte) i);
					chromosomes[i].writeToNBT(chromosomeTag);
					tagList.add(chromosomeTag);
				}
			}
			tagCompound.put(CHROMOSOMES_TAG, tagList);
			return tagCompound;
		}

		@Override
		IChromosome[] readTag(IKaryotype karyotype, CompoundNBT tagCompound) {
			IChromosomeType[] geneTypes = karyotype.getChromosomeTypes();
			ListNBT chromosomesNBT = tagCompound.getList(CHROMOSOMES_TAG, Constants.NBT.TAG_COMPOUND);
			IChromosome[] chromosomes = new IChromosome[geneTypes.length];
			ResourceLocation primaryTemplateIdentifier = null;
			ResourceLocation secondaryTemplateIdentifier = null;

			for (int i = 0; i < chromosomesNBT.size(); i++) {
				CompoundNBT chromosomeNBT = chromosomesNBT.getCompound(i);
				byte chromosomeOrdinal = chromosomeNBT.getByte(SLOT_TAG);

				if (chromosomeOrdinal >= 0 && chromosomeOrdinal < chromosomes.length) {
					IChromosomeType geneType = geneTypes[chromosomeOrdinal];
					Chromosome chromosome = Chromosome.create(primaryTemplateIdentifier, secondaryTemplateIdentifier, geneType, chromosomeNBT);
					chromosomes[chromosomeOrdinal] = chromosome;

					if (geneType.equals(karyotype.getSpeciesType())) {
						primaryTemplateIdentifier = chromosome.getActiveAllele().getRegistryName();
						secondaryTemplateIdentifier = chromosome.getInactiveAllele().getRegistryName();
					}
				}
			}
			return chromosomes;
		}

		@Nullable
		@Override
		IAllele getAlleleDirectly(CompoundNBT genomeNBT, IChromosomeType geneType, boolean active) {
			ListNBT tagList = genomeNBT.getList(CHROMOSOMES_TAG, Constants.NBT.TAG_COMPOUND);
			if (tagList.isEmpty()) {
				return null;
			}
			CompoundNBT chromosomeTag = tagList.getCompound(geneType.getIndex());
			if (chromosomeTag.isEmpty()) {
				return null;
			}
			return (active ? Chromosome.getActiveAllele(chromosomeTag) : Chromosome.getInactiveAllele(chromosomeTag)).orElse(null);
		}

		@Override
		public IChromosome getSpecificChromosome(CompoundNBT genomeNBT, IChromosomeType chromosomeType) {
			IChromosome[] chromosomes = readTag(chromosomeType.getRoot().getKaryotype(), genomeNBT);
			return chromosomes[chromosomeType.getIndex()];
		}

		@Override
		public boolean canLoad(CompoundNBT tagCompound) {
			return tagCompound.contains(CHROMOSOMES_TAG) && tagCompound.contains(VERSION_TAG);
		}
	},
	//Used for backward compatibility because before Forestry 5.8 the first allele was not always the active allele.
	UUID_DEPRECATED {
		@Override
		public CompoundNBT writeTag(IChromosome[] chromosomes, IKaryotype karyotype, CompoundNBT tagCompound) {
			throw new UnsupportedOperationException();
		}

		@SuppressWarnings("deprecation")
		@Override
		IChromosome[] readTag(IKaryotype karyotype, CompoundNBT tagCompound) {
			IChromosomeType[] geneTypes = karyotype.getChromosomeTypes();
			ListNBT chromosomesNBT = tagCompound.getList(CHROMOSOMES_TAG, Constants.NBT.TAG_COMPOUND);
			IChromosome[] chromosomes = new IChromosome[geneTypes.length];
			ResourceLocation primaryTemplateIdentifier = null;
			ResourceLocation secondaryTemplateIdentifier = null;

			for (int i = 0; i < chromosomesNBT.size(); i++) {
				CompoundNBT chromosomeNBT = chromosomesNBT.getCompound(i);
				byte chromosomeOrdinal = chromosomeNBT.getByte(SLOT_TAG);

				if (chromosomeOrdinal >= 0 && chromosomeOrdinal < chromosomes.length) {
					IChromosomeType geneType = geneTypes[chromosomeOrdinal];
					Chromosome chromosome = Chromosome.create(primaryTemplateIdentifier, secondaryTemplateIdentifier, geneType, chromosomeNBT);
					chromosomes[chromosomeOrdinal] = chromosome;

					if (geneType == karyotype.getSpeciesType()) {
						primaryTemplateIdentifier = chromosome.getActiveAllele().getRegistryName();
						secondaryTemplateIdentifier = chromosome.getInactiveAllele().getRegistryName();
					}
				}
			}
			return chromosomes;
		}

		@Nullable
		@Override
		IAllele getAlleleDirectly(CompoundNBT genomeNBT, IChromosomeType geneType, boolean active) {
			ListNBT tagList = genomeNBT.getList(CHROMOSOMES_TAG, Constants.NBT.TAG_COMPOUND);
			if (tagList.isEmpty()) {
				return null;
			}
			CompoundNBT chromosomeTag = tagList.getCompound(geneType.getIndex());
			if (chromosomeTag.isEmpty()) {
				return null;
			}
			IChromosome chromosome = Chromosome.create(null, null, geneType, chromosomeTag);
			return active ? chromosome.getActiveAllele() : chromosome.getInactiveAllele();
		}

		@Override
		public IChromosome getSpecificChromosome(CompoundNBT genomeNBT, IChromosomeType geneType) {
			IChromosome[] chromosomes = readTag(geneType.getRoot().getKaryotype(), genomeNBT);
			return chromosomes[geneType.getIndex()];
		}

		@Override
		public boolean canLoad(CompoundNBT tagCompound) {
			return tagCompound.contains(CHROMOSOMES_TAG);
		}
	},
	//Used to save the chromosomes as compact as possible
	BINARY {
		private static final String DATA_TAG = "data";

		@Override
		CompoundNBT writeTag(IChromosome[] chromosomes, IKaryotype karyotype, CompoundNBT tagCompound) {
			SimpleByteBuf byteBuf = new SimpleByteBuf(chromosomes.length);
			byteBuf.writeChromosomes(chromosomes, karyotype);
			tagCompound.putByteArray(DATA_TAG, byteBuf.toByteArray());
			tagCompound.putInt(VERSION_TAG, VERSION);

			return tagCompound;
		}

		@Override
		IChromosome[] readTag(IKaryotype karyotype, CompoundNBT tagCompound) {
			byte[] data = tagCompound.getByteArray(DATA_TAG);
			SimpleByteBuf simpleByteBuf = new SimpleByteBuf(data);
			return simpleByteBuf.readChromosomes(karyotype);
		}

		@Nullable
		@Override
		IAllele getAlleleDirectly(CompoundNBT genomeNBT, IChromosomeType geneType, boolean active) {
			byte[] data = genomeNBT.getByteArray(DATA_TAG);
			SimpleByteBuf simpleByteBuf = new SimpleByteBuf(data);
			ChromosomeInfo chromosomeInfo = simpleByteBuf.readChromosome(geneType);
			IChromosome chromosome = chromosomeInfo.chromosome;
			if (chromosome == null) {
				return null;
			}
			return active ? chromosome.getActiveAllele() : chromosome.getInactiveAllele();
		}

		@Override
		public IChromosome getSpecificChromosome(CompoundNBT genomeNBT, IChromosomeType geneType) {
			byte[] data = genomeNBT.getByteArray(DATA_TAG);
			SimpleByteBuf simpleByteBuf = new SimpleByteBuf(data);
			ChromosomeInfo chromosomeInfo = simpleByteBuf.readChromosome(geneType);
			if (chromosomeInfo.chromosome == null) {
				//Fix the broken NBT
				return fixData(genomeNBT, chromosomeInfo);
			}
			return chromosomeInfo.chromosome;
		}

		private IChromosome fixData(CompoundNBT genomeNBT, ChromosomeInfo missingChromosome) {
			IChromosomeType geneType = missingChromosome.chromosomeType;
			IKaryotype karyotype = geneType.getRoot().getKaryotype();
			IChromosome[] chromosomes = readTag(karyotype, genomeNBT);
			IChromosome chromosome = Chromosome.create(missingChromosome.activeSpeciesUid, missingChromosome.inactiveSpeciesUid, geneType, null, null);
			chromosomes[geneType.getIndex()] = chromosome;
			writeTag(chromosomes, karyotype, genomeNBT);
			return chromosome;
		}

		@Override
		public boolean canLoad(CompoundNBT tagCompound) {
			return tagCompound.contains(DATA_TAG);
		}
	};

	private static final String VERSION_TAG = "version";
	private static final String SLOT_TAG = "Slot";
	private static final int VERSION = 1;
	private static final String CHROMOSOMES_TAG = "Chromosomes";

	abstract CompoundNBT writeTag(IChromosome[] chromosomes, IKaryotype karyotype, CompoundNBT tagCompound);

	abstract IChromosome[] readTag(IKaryotype karyotype, CompoundNBT tagCompound);

	@Nullable
	abstract IAllele getAlleleDirectly(CompoundNBT genomeNBT, IChromosomeType geneKey, boolean active);

	abstract IChromosome getSpecificChromosome(CompoundNBT genomeNBT, IChromosomeType geneKey);

	abstract boolean canLoad(CompoundNBT tagCompound);
}

package forestry.core.genetics;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.utils.SimpleByteBuf;

public enum SaveFormat {
	//Used before forge fires the FMLLoadCompleteEvent.
	UID {
		@Override
		public NBTTagCompound writeTag(IChromosome[] chromosomes, ISpeciesRoot speciesRoot, NBTTagCompound tagCompound) {
			NBTTagList tagList = new NBTTagList();
			for (int i = 0; i < chromosomes.length; i++) {
				if (chromosomes[i] != null) {
					NBTTagCompound chromosomeTag = new NBTTagCompound();
					chromosomeTag.setByte(SLOT_TAG, (byte) i);
					chromosomes[i].writeToNBT(chromosomeTag);
					tagList.appendTag(chromosomeTag);
				}
			}
			tagCompound.setTag(CHROMOSOMES_TAG, tagList);
			return tagCompound;
		}

		@Override
		public IChromosome[] readTag(ISpeciesRoot speciesRoot, NBTTagCompound tagCompound) {
			NBTTagList chromosomesNBT = tagCompound.getTagList(CHROMOSOMES_TAG, 10);
			IChromosome[] chromosomes = new IChromosome[speciesRoot.getDefaultTemplate().length];
			String primarySpeciesUid = null;
			String secondarySpeciesUid = null;

			for (int i = 0; i < chromosomesNBT.tagCount(); i++) {
				NBTTagCompound chromosomeNBT = chromosomesNBT.getCompoundTagAt(i);
				byte chromosomeOrdinal = chromosomeNBT.getByte(SLOT_TAG);

				if (chromosomeOrdinal >= 0 && chromosomeOrdinal < chromosomes.length) {
					IChromosomeType chromosomeType = speciesRoot.getKaryotype()[chromosomeOrdinal];
					Chromosome chromosome = Chromosome.create(primarySpeciesUid, secondarySpeciesUid, chromosomeType, chromosomeNBT);
					chromosomes[chromosomeOrdinal] = chromosome;

					if (chromosomeOrdinal == speciesRoot.getSpeciesChromosomeType().ordinal()) {
						primarySpeciesUid = chromosome.getActiveAllele().getUID();
						secondarySpeciesUid = chromosome.getInactiveAllele().getUID();
					}
				}
			}
			return chromosomes;
		}

		@Nullable
		@Override
		IAllele getAlleleDirectly(NBTTagCompound genomeNBT, IChromosomeType chromosomeType, boolean active) {
			NBTTagList tagList = genomeNBT.getTagList(CHROMOSOMES_TAG, 10);
			if (tagList.hasNoTags()) {
				return null;
			}
			NBTTagCompound chromosomeTag = tagList.getCompoundTagAt(chromosomeType.ordinal());
			if(chromosomeTag.hasNoTags()){
				return null;
			}
			return active ? Chromosome.getActiveAllele(chromosomeTag) : Chromosome.getInactiveAllele(chromosomeTag);
		}

		@Override
		public IChromosome getSpecificChromosome(NBTTagCompound genomeNBT, IChromosomeType chromosomeType) {
			IChromosome[] chromosomes = readTag(chromosomeType.getSpeciesRoot(), genomeNBT);
			return chromosomes[chromosomeType.ordinal()];
		}

		@Override
		public boolean canLoad(NBTTagCompound tagCompound) {
			return tagCompound.hasKey(CHROMOSOMES_TAG) && tagCompound.hasKey(VERSION_TAG);
		}
	},
	//Used for backward compatibility because before Forestry 5.8 the first allele was not always the active allele.
	UUID_DEPRECATED {
		@Override
		public NBTTagCompound writeTag(IChromosome[] chromosomes, ISpeciesRoot speciesRoot, NBTTagCompound tagCompound) {
			throw new UnsupportedOperationException();
		}

		@SuppressWarnings("deprecation")
		@Override
		public IChromosome[] readTag(ISpeciesRoot speciesRoot, NBTTagCompound tagCompound) {
			NBTTagList chromosomesNBT = tagCompound.getTagList(CHROMOSOMES_TAG, 10);
			IChromosome[] chromosomes = new IChromosome[speciesRoot.getDefaultTemplate().length];
			String primarySpeciesUid = null;
			String secondarySpeciesUid = null;

			for (int i = 0; i < chromosomesNBT.tagCount(); i++) {
				NBTTagCompound chromosomeNBT = chromosomesNBT.getCompoundTagAt(i);
				byte chromosomeOrdinal = chromosomeNBT.getByte(SLOT_TAG);

				if (chromosomeOrdinal >= 0 && chromosomeOrdinal < chromosomes.length) {
					IChromosomeType chromosomeType = speciesRoot.getKaryotype()[chromosomeOrdinal];
					Chromosome chromosome = Chromosome.create(primarySpeciesUid, secondarySpeciesUid, chromosomeType, chromosomeNBT);
					chromosomes[chromosomeOrdinal] = chromosome;

					if (chromosomeOrdinal == speciesRoot.getSpeciesChromosomeType().ordinal()) {
						primarySpeciesUid = chromosome.getActiveAllele().getUID();
						secondarySpeciesUid = chromosome.getInactiveAllele().getUID();
					}
				}
			}
			return chromosomes;
		}

		@Nullable
		@Override
		IAllele getAlleleDirectly(NBTTagCompound genomeNBT, IChromosomeType chromosomeType, boolean active) {
			NBTTagList tagList = genomeNBT.getTagList(CHROMOSOMES_TAG, 10);
			if (tagList.hasNoTags()) {
				return null;
			}
			NBTTagCompound chromosomeTag = tagList.getCompoundTagAt(chromosomeType.ordinal());
			if(chromosomeTag.hasNoTags()){
				return null;
			}
			IChromosome chromosome = Chromosome.create(null, null, chromosomeType, chromosomeTag);
			return active ? chromosome.getActiveAllele() : chromosome.getInactiveAllele();
		}

		@Override
		public IChromosome getSpecificChromosome(NBTTagCompound genomeNBT, IChromosomeType chromosomeType) {
			IChromosome[] chromosomes = readTag(chromosomeType.getSpeciesRoot(), genomeNBT);
			return chromosomes[chromosomeType.ordinal()];
		}

		@Override
		public boolean canLoad(NBTTagCompound tagCompound) {
			return tagCompound.hasKey(CHROMOSOMES_TAG);
		}
	},
	//Used to save the chromosomes as compact as possible
	BINARY {
		private static final String DATA_TAG = "data";

		@Override
		public NBTTagCompound writeTag(IChromosome[] chromosomes, ISpeciesRoot speciesRoot, NBTTagCompound tagCompound) {
			SimpleByteBuf byteBuf = new SimpleByteBuf();
			byteBuf.writeChromosomes(chromosomes, speciesRoot);
			tagCompound.setByteArray(DATA_TAG, byteBuf.toByteArray());
			tagCompound.setInteger(VERSION_TAG, VERSION);

			return tagCompound;
		}

		@Override
		public IChromosome[] readTag(ISpeciesRoot speciesRoot, NBTTagCompound tagCompound) {
			byte[] data = tagCompound.getByteArray(DATA_TAG);
			SimpleByteBuf simpleByteBuf = new SimpleByteBuf(data);
			return simpleByteBuf.readChromosomes(speciesRoot);
		}

		@Nullable
		@Override
		IAllele getAlleleDirectly(NBTTagCompound genomeNBT, IChromosomeType chromosomeType, boolean active) {
			byte[] data = genomeNBT.getByteArray(DATA_TAG);
			SimpleByteBuf simpleByteBuf = new SimpleByteBuf(data);
			ChromosomeInfo chromosomeInfo = simpleByteBuf.readChromosome(chromosomeType);
			IChromosome chromosome = chromosomeInfo.chromosome;
			if(chromosome == null){
				return null;
			}
			return active ? chromosome.getActiveAllele() : chromosome.getInactiveAllele();
		}

		@Override
		public IChromosome getSpecificChromosome(NBTTagCompound genomeNBT, IChromosomeType chromosomeType) {
			byte[] data = genomeNBT.getByteArray(DATA_TAG);
			SimpleByteBuf simpleByteBuf = new SimpleByteBuf(data);
			ChromosomeInfo chromosomeInfo = simpleByteBuf.readChromosome(chromosomeType);
			if(chromosomeInfo.chromosome == null){
				//Fix the broken NBT
				return fixData(genomeNBT, chromosomeInfo);
			}
			return chromosomeInfo.chromosome;
		}

		private IChromosome fixData(NBTTagCompound genomeNBT, ChromosomeInfo missingChromosome){
			IChromosomeType chromosomeType = missingChromosome.chromosomeType;
			ISpeciesRoot speciesRoot = chromosomeType.getSpeciesRoot();
			IChromosome[] chromosomes = readTag(speciesRoot, genomeNBT);
			IChromosome chromosome = Chromosome.create(missingChromosome.activeSpeciesUid, missingChromosome.inactiveSpeciesUid, chromosomeType, null, null);
			chromosomes[chromosomeType.ordinal()] = chromosome;
			writeTag(chromosomes, speciesRoot, genomeNBT);
			return chromosome;
		}

		@Override
		public boolean canLoad(NBTTagCompound tagCompound) {
			return tagCompound.hasKey(DATA_TAG);
		}
	};

	private static final String VERSION_TAG = "version";
	private static final String SLOT_TAG = "Slot";
	private static final int VERSION = 1;
	private static final String CHROMOSOMES_TAG = "Chromosomes";

	abstract NBTTagCompound writeTag(IChromosome[] chromosomes, ISpeciesRoot speciesRoot, NBTTagCompound tagCompound);

	abstract IChromosome[] readTag(ISpeciesRoot speciesRoot, NBTTagCompound tagCompound);

	@Nullable
	abstract IAllele getAlleleDirectly(NBTTagCompound genomeNBT, IChromosomeType chromosomeType, boolean active);

	abstract IChromosome getSpecificChromosome(NBTTagCompound genomeNBT, IChromosomeType chromosomeType);

	abstract boolean canLoad(NBTTagCompound tagCompound);
}

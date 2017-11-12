package forestry.core.genetics;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.utils.SimpleByteBuf;

public enum SaveFormat {
	UID {
		private static final String SLOT_TAG = "Slot";

		@Override
		public NBTTagCompound writeTag(IGenome genome, NBTTagCompound tagCompound) {
			NBTTagList tagList = new NBTTagList();
			IChromosome[] chromosomes = genome.getChromosomes();
			for (int i = 0; i < chromosomes.length; i++) {
				if (chromosomes[i] != null) {
					NBTTagCompound chromosomeTag = new NBTTagCompound();
					chromosomeTag.setByte(SLOT_TAG, (byte) i);
					chromosomes[i].writeToNBT(chromosomeTag);
					tagList.appendTag(chromosomeTag);
				}
			}
			tagCompound.setTag("Chromosomes", tagList);
			return tagCompound;
		}

		@Override
		public IChromosome[] readTag(NBTTagCompound tagCompound, ISpeciesRoot speciesRoot) {
			NBTTagList chromosomesNBT = tagCompound.getTagList("Chromosomes", 10);
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
						primarySpeciesUid = chromosome.getPrimaryAllele().getUID();
						secondarySpeciesUid = chromosome.getSecondaryAllele().getUID();
					}
				}
			}
			return chromosomes;
		}

		@Override
		public IChromosome getChromosomeDirectly(NBTTagCompound genomeNBT, IChromosomeType chromosomeType) {
			NBTTagList tagList = genomeNBT.getTagList("Chromosomes", 10);
			if (tagList.hasNoTags()) {
				return null;
			}
			NBTTagCompound chromosomeTag = tagList.getCompoundTagAt(chromosomeType.ordinal());
			if(chromosomeTag.hasNoTags()){
				return null;
			}
			return Chromosome.create(null, null, chromosomeType, chromosomeTag);
		}

		@Override
		public IChromosome getSpecificChromosome(NBTTagCompound genomeNBT, IChromosomeType chromosomeType) {
			IChromosome[] chromosomes = readTag(genomeNBT, chromosomeType.getSpeciesRoot());
			if(chromosomes.length == 0 || chromosomeType.ordinal() >= chromosomes.length){
				return null;
			}

			return chromosomes[chromosomeType.ordinal()];
		}

		@Override
		public boolean canLoad(NBTTagCompound tagCompound) {
			return tagCompound.hasKey("Chromosomes");
		}
	},
	BINARY {
		private static final String DATA_TAG = "data";
		private static final String VERSION_TAG = "version";
		private static final String VERSION = "1.0.0";

		@Override
		public NBTTagCompound writeTag(IGenome genome, NBTTagCompound tagCompound) {
			ISpeciesRoot speciesRoot = genome.getSpeciesRoot();
			IChromosome[] chromosomes = genome.getChromosomes();

			SimpleByteBuf byteBuf = new SimpleByteBuf();
			byteBuf.writeChromosomes(chromosomes, speciesRoot);
			tagCompound.setByteArray(DATA_TAG, byteBuf.toByteArray());
			tagCompound.setString(VERSION_TAG, VERSION);

			return tagCompound;
		}

		@Override
		public IChromosome[] readTag(NBTTagCompound tagCompound, ISpeciesRoot speciesRoot) {
			byte[] data = tagCompound.getByteArray(DATA_TAG);
			SimpleByteBuf simpleByteBuf = new SimpleByteBuf(data);
			return simpleByteBuf.readChromosomes(speciesRoot);
		}

		@Nullable
		@Override
		public IChromosome getChromosomeDirectly(NBTTagCompound genomeNBT, IChromosomeType chromosomeType) {
			IChromosome[] chromosomes = readTag(genomeNBT, chromosomeType.getSpeciesRoot());
			return chromosomes[chromosomeType.ordinal()];
		}

		@Nullable
		@Override
		public IChromosome getSpecificChromosome(NBTTagCompound genomeNBT, IChromosomeType chromosomeType) {
			IChromosome[] chromosomes = readTag(genomeNBT, chromosomeType.getSpeciesRoot());
			return chromosomes[chromosomeType.ordinal()];
		}

		@Override
		public boolean canLoad(NBTTagCompound tagCompound) {
			return tagCompound.hasKey(DATA_TAG);
		}
	};

	abstract NBTTagCompound writeTag(IGenome genome, NBTTagCompound tagCompound);

	abstract IChromosome[] readTag(NBTTagCompound tagCompound, ISpeciesRoot speciesRoot);

	@Nullable
	abstract IChromosome getChromosomeDirectly(NBTTagCompound genomeNBT, IChromosomeType chromosomeType);

	abstract IChromosome getSpecificChromosome(NBTTagCompound genomeNBT, IChromosomeType chromosomeType);

	abstract boolean canLoad(NBTTagCompound tagCompound);
}

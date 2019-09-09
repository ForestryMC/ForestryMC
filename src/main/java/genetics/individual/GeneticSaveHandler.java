package genetics.individual;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import genetics.api.GeneticHelper;
import genetics.api.IGeneticSaveHandler;
import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleRegistry;
import genetics.api.alleles.IAlleleTemplate;
import genetics.api.individual.IChromosome;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IGenome;
import genetics.api.individual.IIndividual;
import genetics.api.individual.IKaryotype;
import genetics.api.organism.IOrganismHandler;
import genetics.api.organism.IOrganismType;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.ITemplateContainer;

import genetics.ApiInstance;
import genetics.Log;

public enum GeneticSaveHandler implements IGeneticSaveHandler {
	INSTANCE;
	private static final String GENOME_TAG = "Genome";
	private static SaveFormat writeFormat = SaveFormat.UID;

	public static void setWriteFormat(SaveFormat writeFormat) {
		GeneticSaveHandler.writeFormat = writeFormat;
	}

	public CompoundNBT writeTag(IChromosome[] chromosomes, IKaryotype karyotype, CompoundNBT tagCompound) {
		return writeFormat.writeTag(chromosomes, karyotype, tagCompound);
	}

	public IChromosome[] readTag(IKaryotype karyotype, CompoundNBT tagCompound) {
		SaveFormat format = getFormat(tagCompound);
		return format.readTag(karyotype, tagCompound);
	}

	private SaveFormat getFormat(CompoundNBT tagCompound) {
		for (SaveFormat format : SaveFormat.values()) {
			if (format.canLoad(tagCompound)) {
				return format;
			}
		}
		return SaveFormat.UID;
	}

	@Nullable
	public IAllele getAlleleDirectly(CompoundNBT genomeNBT, IChromosomeType chromosomeType, boolean active) {
		SaveFormat format = getFormat(genomeNBT);
		return format.getAlleleDirectly(genomeNBT, chromosomeType, active);
	}

	/**
	 * Quickly gets the species without loading the whole genome. And without creating absent chromosomes.
	 */
	@Nullable
	public IAllele getAlleleDirectly(ItemStack itemStack, IOrganismType type, IChromosomeType chromosomeType, boolean active) {
		CompoundNBT nbtTagCompound = itemStack.getTag();
		if (nbtTagCompound == null || nbtTagCompound.isEmpty()) {
			return null;
		}

		CompoundNBT individualNBT = getIndividualDataDirectly(itemStack, type, chromosomeType.getRoot());
		if (individualNBT == null || individualNBT.isEmpty()) {
			return null;
		}

		CompoundNBT genomeNBT = individualNBT.getCompound(GENOME_TAG);
		if (genomeNBT.isEmpty()) {
			return null;
		}
		IAllele allele = getAlleleDirectly(genomeNBT, chromosomeType, active);
		IAlleleRegistry alleleRegistry = ApiInstance.INSTANCE.getAlleleRegistry();
		if (allele == null || !alleleRegistry.isValidAllele(allele, chromosomeType)) {
			return null;
		}
		return allele;
	}

	// NBT RETRIEVAL

	public IAllele getAllele(ItemStack itemStack, IOrganismType type, IChromosomeType chromosomeType, boolean active) {
		IChromosome chromosome = getSpecificChromosome(itemStack, type, chromosomeType);
		return active ? chromosome.getActiveAllele() : chromosome.getInactiveAllele();
	}

	public IChromosome getSpecificChromosome(CompoundNBT genomeNBT, IChromosomeType chromosomeType) {
		SaveFormat format = getFormat(genomeNBT);
		return format.getSpecificChromosome(genomeNBT, chromosomeType);
	}

	/**
	 * Tries to load a specific chromosome and creates it if it is absent.
	 */
	public IChromosome getSpecificChromosome(ItemStack itemStack, IOrganismType type, IChromosomeType chromosomeType) {
		CompoundNBT nbtTagCompound = itemStack.getTag();
		if (nbtTagCompound == null) {
			nbtTagCompound = new CompoundNBT();
			itemStack.setTag(nbtTagCompound);
		}

		CompoundNBT individualNBT = getIndividualData(itemStack, type, chromosomeType.getRoot());
		CompoundNBT genomeNBT = individualNBT.getCompound(GENOME_TAG);

		return getSpecificChromosome(genomeNBT, chromosomeType);
	}

	@Nullable
	@Override
	public CompoundNBT getIndividualDataDirectly(ItemStack itemStack, IOrganismType type, IIndividualRoot<IIndividual> root) {
		IOrganismHandler organismHandler = GeneticHelper.getOrganismHandler(root, type);
		return organismHandler.getIndividualData(itemStack);
	}

	@Override
	public void setIndividualData(ItemStack itemStack, IOrganismType type, IIndividualRoot<IIndividual> root, CompoundNBT compound) {
		IOrganismHandler organismHandler = GeneticHelper.getOrganismHandler(root, type);
		organismHandler.setIndividualData(itemStack, compound);
	}

	@Override
	public CompoundNBT getIndividualData(ItemStack itemStack, IOrganismType type, IIndividualRoot<IIndividual> root) {
		IOrganismHandler organismHandler = GeneticHelper.getOrganismHandler(root, type);
		CompoundNBT compound = organismHandler.getIndividualData(itemStack);
		if (compound != null) {
			return compound;
		}
		compound = new CompoundNBT();
		CompoundNBT genomeNBT = compound.getCompound(GENOME_TAG);

		if (genomeNBT.isEmpty()) {
			Log.error("Got a genetic item with no genome, setting it to a default value.");
			genomeNBT = new CompoundNBT();

			ITemplateContainer container = root.getTemplates();
			IAlleleTemplate defaultTemplate = container.getKaryotype().getDefaultTemplate();
			IGenome genome = defaultTemplate.toGenome(null);
			genome.writeToNBT(genomeNBT);
			compound.put(GENOME_TAG, genomeNBT);
		}
		organismHandler.setIndividualData(itemStack, compound);
		return compound;
	}
}

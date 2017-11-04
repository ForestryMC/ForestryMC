package forestry.core.genetics;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.ISpeciesRoot;

public class GenomeSaveHandler {

	private  static SaveFormat writeFormat = SaveFormat.UID;

	public  static void setWriteFormat(SaveFormat writeFormat) {
		GenomeSaveHandler.writeFormat = writeFormat;
	}

	public static SaveFormat getFormat(NBTTagCompound tagCompound){
		for(SaveFormat format : SaveFormat.values()){
			if(format.canLoad(tagCompound)){
				return format;
			}
		}
		return SaveFormat.UID;
	}

	public static NBTTagCompound writeTag(IGenome genome, NBTTagCompound tagCompound) {
		return writeFormat.writeTag(genome, tagCompound);
	}

	public static IChromosome[] readTag(NBTTagCompound tagCompound, ISpeciesRoot speciesRoot) {
		SaveFormat format = getFormat(tagCompound);
		return format.readTag(tagCompound, speciesRoot);
	}

	@Nullable
	public static IChromosome getChromosomeDirectly(NBTTagCompound genomeNBT, IChromosomeType chromosomeType) {
		SaveFormat format = getFormat(genomeNBT);
		return format.getChromosomeDirectly(genomeNBT, chromosomeType);
	}

	public static IChromosome getSpecificChromosome(NBTTagCompound genomeNBT, IChromosomeType chromosomeType) {
		SaveFormat format = getFormat(genomeNBT);
		return format.getSpecificChromosome(genomeNBT, chromosomeType);
	}

	/**
	 * Converts the nbt into the current format.
	 */
	public static ItemStack covert(ItemStack itemStack){
		NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
		if (nbtTagCompound == null) {
			return itemStack;
		}

		NBTTagCompound genomeNBT = nbtTagCompound.getCompoundTag(Genome.GENOME_TAG);
		if (genomeNBT.hasNoTags()) {
			return itemStack;
		}
		SaveFormat saveFormat = getFormat(genomeNBT);
		if(saveFormat == SaveFormat.BINARY){
			return itemStack;
		}
		ISpeciesRoot speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(itemStack);
		if(speciesRoot == null){
			return itemStack;
		}
		IIndividual individual = speciesRoot.getMember(itemStack);
		if(individual == null){
			return itemStack;
		}
		ItemStack converted = itemStack.copy();
		NBTTagCompound tagCompound = individual.writeToNBT(new NBTTagCompound());
		converted.setTagCompound(tagCompound);
		return converted;
	}
}

package forestry.core.genetics;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;

import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IGenome;
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
}

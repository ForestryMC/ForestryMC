package forestry.core.genetics;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IChromosomeType;
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

	public static NBTTagCompound writeTag(IChromosome[] chromosomes, ISpeciesRoot speciesRoot, NBTTagCompound tagCompound) {
		return writeFormat.writeTag(chromosomes, speciesRoot, tagCompound);
	}

	public static IChromosome[] readTag(ISpeciesRoot speciesRoot, NBTTagCompound tagCompound) {
		SaveFormat format = getFormat(tagCompound);
		return format.readTag(speciesRoot, tagCompound);
	}

	@Nullable
	public static IAllele getAlleleDirectly(NBTTagCompound genomeNBT, IChromosomeType chromosomeType, boolean active){
		SaveFormat format = getFormat(genomeNBT);
		return format.getAlleleDirectly(genomeNBT, chromosomeType, active);
	}

	public static IChromosome getSpecificChromosome(NBTTagCompound genomeNBT, IChromosomeType chromosomeType) {
		SaveFormat format = getFormat(genomeNBT);
		return format.getSpecificChromosome(genomeNBT, chromosomeType);
	}
}

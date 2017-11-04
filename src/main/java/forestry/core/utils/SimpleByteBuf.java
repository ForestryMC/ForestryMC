package forestry.core.utils;

import java.util.Arrays;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleRegistry;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.genetics.Chromosome;

public class SimpleByteBuf {

	private byte[] data;
	private int index;

	public SimpleByteBuf(int initialCapacity) {
		this.data = new byte[initialCapacity];
	}

	public SimpleByteBuf() {
		this(20);
	}

	public SimpleByteBuf(byte[] data) {
		this.data = data;
	}

	public byte[] toByteArray(){
		return data;
	}

	public void writeAllele(IAllele allele){
		IAlleleRegistry alleleRegistry = AlleleManager.alleleRegistry;
		writeVarInt(alleleRegistry.getAlleleID(allele));
	}

	public IAllele readAllele(){
		IAlleleRegistry alleleRegistry = AlleleManager.alleleRegistry;
		int id = readVarInt();
		return alleleRegistry.getAlleleById(id);
	}

	private int getValidChromosomes(IChromosome[] chromosomes, IChromosomeType[] chromosomeTypes){
		int types = 0;
		for (IChromosomeType type : chromosomeTypes) {
			int index = type.ordinal();
			if (index >= chromosomes.length) {
				types <<= 1;
				continue;
			}
			types = (types << 1) + 1;
		}
		return types;
	}

	public void writeChromosomes(IChromosome[] chromosomes, ISpeciesRoot speciesRoot){
		IChromosomeType[] chromosomeTypes = speciesRoot.getKaryotype();
		writeVarInt(getValidChromosomes(chromosomes, chromosomeTypes));
		for (IChromosomeType type : chromosomeTypes) {
			int index = type.ordinal();
			if (index >= chromosomes.length) {
				continue;
			}
			IChromosome chromosome = chromosomes[type.ordinal()];
			writeAllele(chromosome.getPrimaryAllele());
			writeAllele(chromosome.getSecondaryAllele());
		}
	}

	public IChromosome[] readChromosomes(ISpeciesRoot speciesRoot){
		IChromosomeType[] chromosomeTypes = speciesRoot.getKaryotype();
		int types = readVarInt();
		IChromosome[] chromosomes = new IChromosome[chromosomeTypes.length];

		String primarySpeciesUid = null;
		String secondarySpeciesUid = null;

		for (IChromosomeType type : chromosomeTypes) {
			int index = type.ordinal();
			int bit = (1 << chromosomeTypes.length - index - 1);
			if ((bit & types) != bit) {
				continue;
			}

			Chromosome chromosome = Chromosome.create(primarySpeciesUid, secondarySpeciesUid, type, readAllele(), readAllele());
			chromosomes[index] = chromosome;

			if (index == speciesRoot.getSpeciesChromosomeType().ordinal()) {
				primarySpeciesUid = chromosome.getPrimaryAllele().getUID();
				secondarySpeciesUid = chromosome.getSecondaryAllele().getUID();
			}
		}
		return chromosomes;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public byte readByte(){
		if(index < 0 || index >= data.length){
			return -1;
		}
		return data[index++];
	}

	private void ensureCapacity(int capacity){
		if(capacity - data.length > 0){
			data = Arrays.copyOf(data, data.length + 1);
		}
	}

	public void writeByte(int input){
		if(input > 255){
			throw new IllegalArgumentException();
		}
		ensureCapacity(index + 1);
		data[index++] = (byte) input;
	}

	public int readVarInt() {
		int output = 0;
		int byteCount = 0;
		while (true) {
			byte b0 = readByte();
			output |= (b0 & 127) << byteCount++ * 7;

			if (byteCount > 5) {
				throw new RuntimeException("VarInt too big");
			}

			if ((b0 & 128) != 128) {
				break;
			}
		}
		return output;
	}

	public void writeVarInt(int input) {
		while ((input & -128) != 0) {
			writeByte((byte) (input & 127 | 128));
			input >>>= 7;
		}

		writeByte((byte)input);
	}
}

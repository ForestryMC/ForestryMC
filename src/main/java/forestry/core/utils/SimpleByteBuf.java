package forestry.core.utils;

import javax.annotation.Nullable;
import java.util.Arrays;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.genetics.Chromosome;
import forestry.core.genetics.ChromosomeInfo;
import forestry.core.genetics.alleles.AlleleRegistry;

/**
 * A byte buffer that can be used to encode and decode chromosomes and alleles into a byte array.
 */
public class SimpleByteBuf {

	/**
	 * Using 22 as a default capacity because that is the default size of an encoded tree genome.
	 */
	private static final int DEFAULT_INITIAL_CAPACITY = 22;

	private byte[] data;
	/**
	 * The current index at that the buffer writes or reads the next byte.
	 */
	private int index;

	/**
	 * Creates a byte buffer with a specific initial capacity.
	 *
	 * @param initialCapacity The initial capacity that the internal array should have.
	 */
	public SimpleByteBuf(int initialCapacity) {
		this.data = new byte[initialCapacity];
	}

	/**
	 * Creates a byte buffer with the default initial capacity.
	 */
	public SimpleByteBuf() {
		this(DEFAULT_INITIAL_CAPACITY);
	}

	/**
	 * Creates a byte buffer that uses the give byte array.
	 */
	public SimpleByteBuf(byte[] data) {
		this.data = data;
	}

	/**
	 * @return A array with all data that this byte buffer contains.
	 */
	public byte[] toByteArray(){
		return data;
	}

	/**
	 * Write the internal id of the allele to the byte array as a varint.
	 */
	public void writeAllele(IAllele allele){
		AlleleRegistry alleleRegistry = AlleleRegistry.getInstance();
		int id = alleleRegistry.getAlleleID(allele);
		if(id < 0){
			writeVarInt(0);
			return;
		}
		writeVarInt(id);
	}

	/**
	 * Read a allele from the byte array using the internal id of the allele written to the array as a varint.
	 */
	@Nullable
	public IAllele readAllele(){
		AlleleRegistry alleleRegistry = AlleleRegistry.getInstance();
		int id = readVarInt();
		return alleleRegistry.getAlleleById(id);
	}

	/**
	 * Every bit of this integer represents a chromosome type of chromosome array.
	 * If the bit is one, the array contains a chromosome at the position of the chromosome type.
	 * If the bit is zero, the array not contains a chromosome at the position of the chromosome type.
	 *
	 * The method {@link #readChromosomes(ISpeciesRoot)} uses this integer do check if a byte array contains the data
	 * of a specific chromosome.
	 */
	private int getValidChromosomes(IChromosome[] chromosomes, IChromosomeType[] chromosomeTypes){
		int types = 0;
		for (IChromosomeType type : chromosomeTypes) {
			int index = type.ordinal();
			if (index >= chromosomes.length || chromosomes[index] == null) {
				types <<= 1;
				continue;
			}
			types = (types << 1) + 1;
		}
		return types;
	}

	/**
	 * Writes the chromosomes to the internal byte array.
	 *
	 * @param chromosomes The chromosomes that should be written.
	 * @param speciesRoot The species root of the genome that contains the chromosomes.
	 */
	public void writeChromosomes(IChromosome[] chromosomes, ISpeciesRoot speciesRoot){
		IChromosomeType[] chromosomeTypes = speciesRoot.getKaryotype();
		writeVarInt(getValidChromosomes(chromosomes, chromosomeTypes));
		for (IChromosomeType type : chromosomeTypes) {
			int index = type.ordinal();
			if (index >= chromosomes.length) {
				continue;
			}
			IChromosome chromosome = chromosomes[type.ordinal()];
			writeAllele(chromosome.getActiveAllele());
			writeAllele(chromosome.getInactiveAllele());
		}
	}

	/**
	 * Reads chromosomes from the byte array.
	 *
	 * @param speciesRoot The species root of the genome that contains the chromosomes.
	 *
	 * @return The chromosome that were read.
	 */
	public IChromosome[] readChromosomes(ISpeciesRoot speciesRoot){
		IChromosomeType[] chromosomeTypes = speciesRoot.getKaryotype();
		int types = readVarInt();
		IChromosome[] chromosomes = new IChromosome[chromosomeTypes.length];

		String primarySpeciesUid = null;
		String secondarySpeciesUid = null;

		for (IChromosomeType type : chromosomeTypes) {
			int index = type.ordinal();
			int bit = (1 << chromosomeTypes.length - index - 1);
			boolean isValid = (bit & types) == bit;

			Chromosome chromosome = readChromosome(type, isValid, primarySpeciesUid, secondarySpeciesUid);
			chromosomes[index] = chromosome;

			if (type == speciesRoot.getSpeciesChromosomeType()) {
				primarySpeciesUid = chromosome.getActiveAllele().getUID();
				secondarySpeciesUid = chromosome.getInactiveAllele().getUID();
			}
		}
		return chromosomes;
	}

	private Chromosome readChromosome(IChromosomeType type, boolean isValid, ChromosomeInfo info){
		return readChromosome(type, isValid, info.activeSpeciesUid, info.inactiveSpeciesUid);
	}

	private Chromosome readChromosome(IChromosomeType type, boolean isValid, @Nullable String activeSpeciesUid, @Nullable String inactiveSpeciesUid){
		IAllele firstAllele = null;
		IAllele secondAllele = null;

		if (isValid) {
			firstAllele = readAllele();
			secondAllele = readAllele();
		}

		return Chromosome.create(activeSpeciesUid, inactiveSpeciesUid, type, firstAllele, secondAllele);
	}

	/**
	 * Reads a specific chromosome from the byte array without creating the whole chromosome array.
	 */
	public ChromosomeInfo readChromosome(IChromosomeType chromosomeType){
		ISpeciesRoot speciesRoot = chromosomeType.getSpeciesRoot();
		IChromosomeType[] chromosomeTypes = speciesRoot.getKaryotype();
		int types = readVarInt();

		ChromosomeInfo info = new ChromosomeInfo(chromosomeType);

		for (IChromosomeType type : chromosomeTypes) {
			int index = type.ordinal();
			int bit = (1 << chromosomeTypes.length - index - 1);
			boolean isValid = (bit & types) == bit;
			if(type == chromosomeType){
				if(!isValid){
					return info;
				}
				return info.setChromosome(readChromosome(type, true, info));
			} else if(type == speciesRoot.getSpeciesChromosomeType()){
				Chromosome chromosome = readChromosome(type, isValid, info);

				info.setSpeciesInfo(chromosome.getActiveAllele().getUID(), chromosome.getInactiveAllele().getUID());
			}else if(isValid){
				readVarInt();
				readVarInt();
			}
		}
		return info;
	}

	/**
	 * Checks if the byte array has a specific length. If the array is to short, it creates a new array with the length
	 * and copies the content from the old array into the new array.
	 */
	private void ensureCapacity(int capacity){
		if(capacity - data.length > 0){
			data = Arrays.copyOf(data, data.length + 1);
		}
	}

	/**
	 * Write a byte to the array
	 */
	private void writeByte(int input){
		if(input > 255){
			throw new IllegalArgumentException();
		}
		ensureCapacity(index + 1);
		data[index++] = (byte) input;
	}

	/**
	 * Read a byte from the array.
	 */
	private byte readByte(){
		if(index < 0 || index >= data.length){
			return -1;
		}
		return data[index++];
	}

	/**
	 * Read a varint from the byte array.
	 */
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

	/**
	 * Write a varint to the array.
	 */
	public void writeVarInt(int input) {
		while ((input & -128) != 0) {
			writeByte((byte) (input & 127 | 128));
			input >>>= 7;
		}

		writeByte((byte)input);
	}
}

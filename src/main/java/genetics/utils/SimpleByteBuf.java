package genetics.utils;

import javax.annotation.Nullable;
import java.util.Arrays;

import net.minecraft.util.ResourceLocation;

import genetics.api.alleles.IAllele;
import genetics.api.individual.IChromosome;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IKaryotype;

import genetics.ApiInstance;
import genetics.alleles.AlleleRegistry;
import genetics.individual.Chromosome;
import genetics.individual.ChromosomeInfo;

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
	private int bufferIndex;

	/**
	 * Creates a byte buffer with the default initial capacity.
	 */
	public SimpleByteBuf() {
		this(DEFAULT_INITIAL_CAPACITY);
	}

	/**
	 * Creates a byte buffer with a specific initial capacity.
	 *
	 * @param initialCapacity The initial capacity that the internal array should have.
	 */
	public SimpleByteBuf(int initialCapacity) {
		this.data = new byte[initialCapacity];
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
	public byte[] toByteArray() {
		return data;
	}

	/**
	 * Writes the chromosomes to the internal byte array.
	 *
	 * @param chromosomes The chromosomes that should be written.
	 * @param karyotype   The species root of the genome that contains the chromosomes.
	 */
	public void writeChromosomes(IChromosome[] chromosomes, IKaryotype karyotype) {
		for (IChromosomeType type : karyotype) {
			int index = type.getIndex();
			if (index >= chromosomes.length) {
				continue;
			}
			IChromosome chromosome = chromosomes[index];
			writeAllele(chromosome.getActiveAllele());
			writeAllele(chromosome.getInactiveAllele());
		}
	}

	/**
	 * Write the internal id of the allele to the byte array as a varint.
	 */
	private void writeAllele(IAllele allele) {
		AlleleRegistry registry = ApiInstance.INSTANCE.alleleRegistry;
		int id = registry == null ? -1 : registry.getId(allele);
		if (id < 0) {
			writeVarInt(0);
			return;
		}
		writeVarInt(id);
	}

	/**
	 * Write a varint to the array.
	 */
	private void writeVarInt(int input) {
		while ((input & -128) != 0) {
			writeByte((byte) (input & 127 | 128));
			input >>>= 7;
		}

		writeByte((byte) input);
	}

	/**
	 * Write a byte to the array
	 */
	private void writeByte(int input) {
		if (input > 255) {
			throw new IllegalArgumentException();
		}
		ensureCapacity(bufferIndex + 1);
		data[bufferIndex++] = (byte) input;
	}

	/**
	 * Checks if the byte array has a specific length. If the array is to short, it creates a new array with the length
	 * and copies the content from the old array into the new array.
	 */
	private void ensureCapacity(int capacity) {
		if (capacity - data.length > 0) {
			data = Arrays.copyOf(data, data.length + 1);
		}
	}

	/**
	 * Reads chromosomes from the byte array.
	 *
	 * @param karyotype The species root of the genome that contains the chromosomes.
	 * @return The chromosome that were read.
	 */
	public IChromosome[] readChromosomes(IKaryotype karyotype) {
		IChromosomeType[] types = karyotype.getChromosomeTypes();
		IChromosome[] chromosomes = new IChromosome[types.length];

		ResourceLocation primaryTemplateIdentifier = null;
		ResourceLocation secondaryTemplateIdentifier = null;

		for (IChromosomeType type : types) {
			int index = type.getIndex();

			Chromosome chromosome = readChromosome(type, primaryTemplateIdentifier, secondaryTemplateIdentifier);
			chromosomes[index] = chromosome;

			if (type.equals(karyotype.getSpeciesType())) {
				primaryTemplateIdentifier = chromosome.getActiveAllele().getRegistryName();
				secondaryTemplateIdentifier = chromosome.getInactiveAllele().getRegistryName();
			}
		}
		return chromosomes;
	}

	@SuppressWarnings("unchecked")
	private Chromosome readChromosome(IChromosomeType type, @Nullable ResourceLocation activeSpeciesUid, @Nullable ResourceLocation inactiveSpeciesUid) {
		IAllele firstAllele = readAllele();
		IAllele secondAllele = readAllele();
		return Chromosome.create(activeSpeciesUid, inactiveSpeciesUid, type, firstAllele, secondAllele);
	}

	/**
	 * Read a allele from the byte array using the internal id of the allele written to the array as a varint.
	 */
	@Nullable
	private IAllele readAllele() {
		AlleleRegistry registry = ApiInstance.INSTANCE.alleleRegistry;
		int id = readVarInt();
		if (registry == null) {
			return null;
		}
		return registry.getAllele(id);
	}

	/**
	 * Read a varint from the byte array.
	 */
	private int readVarInt() {
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
	 * Read a byte from the array.
	 */
	private byte readByte() {
		if (bufferIndex < 0 || bufferIndex >= data.length) {
			return -1;
		}
		return data[bufferIndex++];
	}

	private Chromosome readChromosome(IChromosomeType type, ChromosomeInfo info) {
		return readChromosome(type, info.activeSpeciesUid, info.inactiveSpeciesUid);
	}

	/**
	 * Reads a specific chromosome from the byte array without creating the whole chromosome array.
	 */
	public ChromosomeInfo readChromosome(IChromosomeType geneType) {
		IKaryotype karyotype = geneType.getRoot().getKaryotype();
		IChromosomeType[] keys = karyotype.getChromosomeTypes();
		ChromosomeInfo info = new ChromosomeInfo(geneType);

		for (IChromosomeType key : keys) {
			if (geneType.equals(key)) {
				return info.setChromosome(readChromosome(key, info));
			} else if (karyotype.getSpeciesType().equals(key)) {
				Chromosome chromosome = readChromosome(key, info);

				info.setSpeciesInfo(chromosome.getActiveAllele().getRegistryName(), chromosome.getInactiveAllele().getRegistryName());
			} else {
				readVarInt();
				readVarInt();
			}
		}
		return info;
	}
}

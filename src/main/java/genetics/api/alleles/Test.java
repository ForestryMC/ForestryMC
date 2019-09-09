package genetics.api.alleles;

public class Test {

	public class Allele {
		public int index;
		public String name;
	}

	public class Genome {
		public Chromosome[] chromosomes;
	}

	public class Chromosome {
		public Allele[] alleles;
	}
}

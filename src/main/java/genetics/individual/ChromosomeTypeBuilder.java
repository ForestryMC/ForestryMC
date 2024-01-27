package genetics.individual;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;

import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleValue;
import genetics.api.individual.IChromosomeAllele;
import genetics.api.individual.IChromosomeList;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IChromosomeTypeBuilder;
import genetics.api.individual.IChromosomeValue;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.IRootDefinition;

public class ChromosomeTypeBuilder implements IChromosomeTypeBuilder {

	private final ChromosomeList list;
	@Nullable
	private String name;

	public ChromosomeTypeBuilder(ChromosomeList list) {
		this.list = list;
	}

	@Override
	public IChromosomeTypeBuilder name(String name) {
		this.name = name;
		return this;
	}

	@Override
	public <V> IChromosomeValue<V> asValue(Class<? extends V> valueClass) {
		Preconditions.checkNotNull(name, "A chromosome type must have a name that is not null");
		return list.add(new Value<>(list.size(), name, list, valueClass));
	}

	@Override
	public <A extends IAllele> IChromosomeAllele<A> asAllele(Class<? extends A> alleleClass) {
		Preconditions.checkNotNull(name, "A chromosome type must have a name that is not null");
		return list.add(new Allele<>(list.size(), name, list, alleleClass));
	}

	private abstract static class Type implements IChromosomeType {

		private final int index;
		private final String name;
		private final IChromosomeList list;

		public Type(int index, String name, IChromosomeList list) {
			this.index = index;
			this.name = name;
			this.list = list;
		}

		@Override
		public int getIndex() {
			return index;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public Component getDisplayName() {
			return Component.translatable("for.gui." + name);
		}

		@Override
		public IIndividualRoot getRoot() {
			return list.getRoot();
		}

		@Override
		public IRootDefinition getDefinition() {
			return list.getDefinition();
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private static class Allele<A extends IAllele> extends Type implements IChromosomeAllele<A> {
		private final Class<? extends A> alleleClass;

		public Allele(int index, String name, IChromosomeList list, Class<? extends A> alleleClass) {
			super(index, name, list);
			this.alleleClass = alleleClass;
		}

		@Override
		public Class<? extends A> getAlleleClass() {
			return alleleClass;
		}

		@Override
		public A castAllele(IAllele allele) {
			return alleleClass.cast(allele);
		}

		@Override
		public boolean isValid(IAllele allele) {
			return alleleClass.isInstance(allele);
		}
	}

	private static class Value<V> extends Type implements IChromosomeValue<V> {
		private final Class<? extends V> valueClass;

		public Value(int index, String name, IChromosomeList list, Class<? extends V> valueClass) {
			super(index, name, list);
			this.valueClass = valueClass;
		}

		@Override
		public Class<? extends V> getValueClass() {
			return valueClass;
		}

		@Override
		public boolean isValid(IAllele allele) {
			if (!(allele instanceof IAlleleValue alleleValue)) {
				return false;
			}
			return valueClass.isInstance(alleleValue.getValue());
		}
	}

}

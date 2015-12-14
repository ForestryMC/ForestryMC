package forestry.core.genetics.alleles;

import java.util.Locale;

import forestry.api.apiculture.FlowerManager;
import forestry.api.genetics.EnumTolerance;
import forestry.apiculture.flowers.FlowerProvider;
import forestry.core.utils.vect.IVect;
import forestry.core.utils.vect.Vect;

public class EnumAllele {
	public enum Fertility implements IAlleleValue<Integer> {
		LOW(1, true),
		NORMAL(2, true),
		HIGH(3),
		MAXIMUM(4);

		private final Integer value;
		private final boolean dominant;

		Fertility(Integer value) {
			this(value, false);
		}

		Fertility(Integer value, boolean dominant) {
			this.value = value;
			this.dominant = dominant;
		}

		@Override
		public Integer getValue() {
			return value;
		}

		@Override
		public boolean isDominant() {
			return dominant;
		}
	}

	public enum Fireproof implements IAlleleValue<Boolean> {
		TRUE(true),
		FALSE(false);

		private final boolean value;
		private final boolean dominant;

		Fireproof(boolean value) {
			this(value, false);
		}

		Fireproof(boolean value, boolean dominant) {
			this.value = value;
			this.dominant = dominant;
		}

		@Override
		public boolean isDominant() {
			return dominant;
		}

		@Override
		public Boolean getValue() {
			return value;
		}
	}

	public enum Flowering implements IAlleleValue<Integer> {
		SLOWEST(5, true),
		SLOWER(10),
		SLOW(15),
		AVERAGE(20),
		FAST(25),
		FASTER(30),
		FASTEST(35),
		MAXIMUM(99, true);

		private final Integer value;
		private final boolean dominant;

		Flowering(Integer value) {
			this(value, false);
		}

		Flowering(Integer value, boolean dominant) {
			this.value = value;
			this.dominant = dominant;
		}

		@Override
		public Integer getValue() {
			return value;
		}

		@Override
		public boolean isDominant() {
			return dominant;
		}
	}

	public enum Flowers implements IAlleleValue<FlowerProvider> {
		VANILLA(FlowerManager.FlowerTypeVanilla, true),
		NETHER(FlowerManager.FlowerTypeNether),
		CACTI(FlowerManager.FlowerTypeCacti),
		MUSHROOMS(FlowerManager.FlowerTypeMushrooms),
		END(FlowerManager.FlowerTypeEnd),
		JUNGLE(FlowerManager.FlowerTypeJungle),
		SNOW(FlowerManager.FlowerTypeSnow, true),
		WHEAT(FlowerManager.FlowerTypeWheat, true),
		GOURD(FlowerManager.FlowerTypeGourd, true);

		private final FlowerProvider value;
		private final boolean dominant;

		Flowers(String flowerType) {
			this(flowerType, false);
		}

		Flowers(String flowerType, boolean dominant) {
			String lowercaseName = toString().toLowerCase(Locale.ENGLISH);
			this.value = new FlowerProvider(flowerType, "flowers." + lowercaseName);
			this.dominant = dominant;
		}

		@Override
		public FlowerProvider getValue() {
			return value;
		}

		@Override
		public boolean isDominant() {
			return dominant;
		}
	}

	public enum Height implements IAlleleValue<Float> {
		SMALLEST(0.25f),
		SMALLER(0.5f),
		SMALL(0.75f),
		AVERAGE(1.0f),
		LARGE(1.25f),
		LARGER(1.5f),
		LARGEST(1.75f),
		GIGANTIC(2.0f);

		private final float value;
		private final boolean dominant;

		Height(float value) {
			this(value, false);
		}

		Height(float value, boolean dominant) {
			this.value = value;
			this.dominant = dominant;
		}

		@Override
		public Float getValue() {
			return value;
		}

		@Override
		public boolean isDominant() {
			return dominant;
		}
	}

	public enum Lifespan implements IAlleleValue<Integer> {
		SHORTEST(10),
		SHORTER(20, true),
		SHORT(30, true),
		SHORTENED(35, true),
		NORMAL(40),
		ELONGATED(45, true),
		LONG(50),
		LONGER(60),
		LONGEST(70);

		private final Integer value;
		private final boolean dominant;

		Lifespan(Integer value) {
			this(value, false);
		}

		Lifespan(Integer value, boolean dominant) {
			this.value = value;
			this.dominant = dominant;
		}

		@Override
		public Integer getValue() {
			return value;
		}

		@Override
		public boolean isDominant() {
			return dominant;
		}
	}

	public enum Maturation implements IAlleleValue<Integer> {
		SLOWEST(10, true),
		SLOWER(7),
		SLOW(5, true),
		AVERAGE(4),
		FAST(3),
		FASTER(2),
		FASTEST(1);

		private final Integer value;
		private final boolean dominant;

		Maturation(Integer value) {
			this(value, false);
		}

		Maturation(Integer value, boolean dominant) {
			this.value = value;
			this.dominant = dominant;
		}

		@Override
		public Integer getValue() {
			return value;
		}

		@Override
		public boolean isDominant() {
			return dominant;
		}
	}

	public enum Saplings implements IAlleleValue<Float> {
		LOWEST(0.01f, true),
		LOWER(0.025f, true),
		LOW(0.035f, true),
		AVERAGE(0.05f, true),
		HIGH(0.1f, true),
		HIGHER(0.2f, true),
		HIGHEST(0.3f, true);

		private final float value;
		private final boolean dominant;

		Saplings(float value) {
			this(value, false);
		}

		Saplings(float value, boolean dominant) {
			this.value = value;
			this.dominant = dominant;
		}

		@Override
		public Float getValue() {
			return value;
		}

		@Override
		public boolean isDominant() {
			return dominant;
		}
	}

	public enum Sappiness implements IAlleleValue<Float> {
		LOWEST(0.1f, true),
		LOWER(0.2f, true),
		LOW(0.3f, true),
		AVERAGE(0.4f, true),
		HIGH(0.6f, true),
		HIGHER(0.8f),
		HIGHEST(1.0f);

		private final Float value;
		private final boolean dominant;

		Sappiness(Float value) {
			this(value, false);
		}

		Sappiness(Float value, boolean dominant) {
			this.value = value;
			this.dominant = dominant;
		}

		@Override
		public Float getValue() {
			return value;
		}

		@Override
		public boolean isDominant() {
			return dominant;
		}
	}

	public enum Size implements IAlleleValue<Float> {
		SMALLEST(0.3f),
		SMALLER(0.4f),
		SMALL(0.5f),
		AVERAGE(0.6f),
		LARGE(0.75f),
		LARGER(0.9f),
		LARGEST(1.0f);

		private final float value;
		private final boolean dominant;

		Size(float value) {
			this(value, false);
		}

		Size(float value, boolean dominant) {
			this.value = value;
			this.dominant = dominant;
		}

		@Override
		public Float getValue() {
			return value;
		}

		@Override
		public boolean isDominant() {
			return dominant;
		}
	}

	public enum Speed implements IAlleleValue<Float> {
		SLOWEST(0.3f, true),
		SLOWER(0.6f, true),
		SLOW(0.8f, true),
		NORMAL(1.0f),
		FAST(1.2f, true),
		FASTER(1.4f),
		FASTEST(1.7f);

		private final float value;
		private final boolean dominant;

		Speed(float value) {
			this(value, false);
		}

		Speed(float value, boolean dominant) {
			this.value = value;
			this.dominant = dominant;
		}

		@Override
		public Float getValue() {
			return value;
		}

		@Override
		public boolean isDominant() {
			return dominant;
		}
	}

	public enum Territory implements IAlleleValue<IVect> {
		AVERAGE(9, 6, 9),
		LARGE(11, 8, 11),
		LARGER(13, 12, 13),
		LARGEST(15, 13, 15);

		private final IVect area;
		private final boolean dominant;

		Territory(int x, int y, int z) {
			this(x, y, z, false);
		}

		Territory(int x, int y, int z, boolean dominant) {
			this.area = new Vect(x, y, z);
			this.dominant = dominant;
		}

		@Override
		public IVect getValue() {
			return area;
		}

		@Override
		public boolean isDominant() {
			return dominant;
		}
	}

	public enum Tolerance implements IAlleleValue<EnumTolerance> {
		NONE,
		BOTH_1(true), BOTH_2, BOTH_3, BOTH_4, BOTH_5,
		UP_1(true), UP_2, UP_3, UP_4, UP_5,
		DOWN_1(true), DOWN_2, DOWN_3, DOWN_4, DOWN_5;

		private final EnumTolerance value;
		private final boolean dominant;

		Tolerance() {
			this(false);
		}

		Tolerance(boolean dominant) {
			this.value = EnumTolerance.values()[ordinal()];
			this.dominant = dominant;
		}

		@Override
		public String toString() {
			return super.toString().replace("_", "");
		}

		@Override
		public EnumTolerance getValue() {
			return value;
		}

		@Override
		public boolean isDominant() {
			return dominant;
		}
	}

	public enum Yield implements IAlleleValue<Float> {
		LOWEST(0.025f, true),
		LOWER(0.05f, true),
		LOW(0.1f, true),
		AVERAGE(0.2f, true),
		HIGH(0.3f),
		HIGHER(0.35f),
		HIGHEST(0.4f);

		private final float value;
		private final boolean dominant;

		Yield(float value) {
			this(value, false);
		}

		Yield(float value, boolean dominant) {
			this.value = value;
			this.dominant = dominant;
		}

		@Override
		public Float getValue() {
			return value;
		}

		@Override
		public boolean isDominant() {
			return dominant;
		}
	}
}

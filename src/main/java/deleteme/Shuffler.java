package deleteme;

import net.minecraft.util.RandomSource;

import java.util.List;

public class Shuffler {

	public static <T> void shuffle(List<T> list, RandomSource rand) {
		int i = list.size();

		for (int j = i; j > 1; --j) {
			list.set(j - 1, list.set(rand.nextInt(j), list.get(j - 1)));
		}
	}
}

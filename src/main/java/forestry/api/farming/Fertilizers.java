package forestry.api.farming;

import net.minecraft.item.Item;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * @author estebes
 */
public class Fertilizers {
	/**
	 * Used to add fertilizers to farms.
	 * Params are an item (the fertilizer item) and the fertilizer value of said item.
	 */
	@Nonnull
	public static final Map<Item, Integer> fertilizers = new HashMap<Item, Integer>();
}

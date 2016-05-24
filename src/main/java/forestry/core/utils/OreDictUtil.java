package forestry.core.utils;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.item.ItemStack;

import net.minecraftforge.oredict.OreDictionary;

public class OreDictUtil {
	public static final String ORE_COPPER = "oreCopper";
	public static final String ORE_TIN = "oreTin";
	public static final String ORE_APATITE = "oreApatite";
	public static final String BLOCK_APATITE = "blockApatite";
	public static final String BLOCK_COPPER = "blockCopper";
	public static final String BLOCK_TIN = "blockTin";
	public static final String BLOCK_BRONZE = "blockBronze";

	public static final String CRAFTING_TABLE_WOOD = "craftingTableWood";

	public static final String BEE_COMB = "beeComb";
	public static final String DROP_ROYAL_JELLY = "dropRoyalJelly";
	public static final String DROP_HONEYDEW = "dropHoneydew";
	public static final String ITEM_POLLEN = "itemPollen";
	public static final String DROP_HONEY = "dropHoney";

	public static final String TREE_SAPLING = "treeSapling";
	public static final String LOG_WOOD = "logWood";
	public static final String PLANK_WOOD = "plankWood";
	public static final String SLAB_WOOD = "slabWood";
	public static final String FENCE_WOOD = "fenceWood";
	public static final String FENCE_GATE_WOOD = "fenceGateWood";
	public static final String STAIR_WOOD = "stairWood";
	public static final String DOOR_WOOD = "doorWood";
	public static final String TREE_LEAVES = "treeLeaves";

	public static final String EMPTIED_LETTER_ORE_DICT = "emptiedLetter";

	public static final String CROP_CHERRY = "cropCherry";
	public static final String CROP_WALNUT = "cropWalnut";
	public static final String CROP_CHESTNUT = "cropChestnut";
	public static final String CROP_LEMON = "cropLemon";
	public static final String CROP_PLUM = "cropPlum";
	public static final String CROP_DATE = "cropDate";
	public static final String CROP_PAPAYA = "cropPapaya";

	/**
	 * Used for ores and metals where conflicts are common.
	 * This way, Forestry can choose whatever is first in the oreDictionary instead of its own metals.
	 * If more mods do this, they will automatically unify on one set of metals.
	 */
	@Nullable
	public static ItemStack getFirstSuitableOre(String oreName) {
		if (OreDictionary.doesOreNameExist(oreName)) {
			List<ItemStack> ores = OreDictionary.getOres(oreName);
			for (ItemStack ore : ores) {
				if (ore != null && ore.getItem() != null && ore.getMetadata() != OreDictionary.WILDCARD_VALUE) {
					ItemStack oreCopy = ore.copy();
					oreCopy.stackSize = 1;
					return oreCopy;
				}
			}
		}
		return null;
	}
}

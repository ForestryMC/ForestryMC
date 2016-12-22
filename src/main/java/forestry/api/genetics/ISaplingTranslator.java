package forestry.api.genetics;

import javax.annotation.Nullable;

import forestry.api.arboriculture.ITree;
import net.minecraft.item.ItemStack;

public interface ISaplingTranslator {
	@Nullable
	ITree getTreeFromSapling(ItemStack sapling);
}

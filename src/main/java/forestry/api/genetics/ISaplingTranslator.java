package forestry.api.genetics;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import forestry.api.arboriculture.ITree;

public interface ISaplingTranslator {
	@Nullable
	ITree getTreeFromSapling(ItemStack sapling);
}

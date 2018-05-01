package forestry.worktable.compat;

import com.google.common.base.Preconditions;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.utils.JeiUtil;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;
import forestry.worktable.ModuleWorktable;
import forestry.worktable.blocks.BlockRegistryWorktable;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;

@JEIPlugin
@SideOnly(Side.CLIENT)
public class WorktableJeiPlugin implements IModPlugin {

	@Override
	public void register(IModRegistry registry) {
		if (!ModuleHelper.isEnabled(ForestryModuleUids.WORKTABLE)) {
			return;
		}
		BlockRegistryWorktable blocks = ModuleWorktable.getBlocks();
		Preconditions.checkNotNull(blocks);

		registry.addRecipeCatalyst(new ItemStack(blocks.worktable), VanillaRecipeCategoryUid.CRAFTING);

		IRecipeTransferRegistry transferRegistry = registry.getRecipeTransferRegistry();
		transferRegistry.addRecipeTransferHandler(new WorktableRecipeTransferHandler(), VanillaRecipeCategoryUid.CRAFTING);

		JeiUtil.addDescription(registry, blocks.worktable);
	}
}

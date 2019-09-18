package forestry.mail.features;

import java.util.function.Function;

import net.minecraft.item.BlockItem;

import forestry.core.config.Constants;
import forestry.core.items.ItemBlockForestry;
import forestry.mail.blocks.BlockMail;
import forestry.mail.blocks.BlockTypeMail;
import forestry.modules.ForestryModuleUids;
import forestry.modules.features.FeatureBlockGroup;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

public class MailBlocks {
	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(Constants.MOD_ID).getRegistry(ForestryModuleUids.MAIL);

	public static final FeatureBlockGroup<BlockMail, BlockTypeMail> BASE = REGISTRY.blockGroup(BlockMail::new, BlockTypeMail.VALUES).setItem((Function<BlockMail, BlockItem>) ItemBlockForestry::new).create();

	private MailBlocks() {
	}
}

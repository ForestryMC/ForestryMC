package forestry.mail.features;

import forestry.core.items.ItemBlockForestry;
import forestry.mail.ModuleMail;
import forestry.mail.blocks.BlockMail;
import forestry.mail.blocks.BlockTypeMail;
import forestry.modules.features.FeatureBlockGroup;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

@FeatureProvider
public class MailBlocks {
    private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleMail.class);

    public static final FeatureBlockGroup<BlockMail, BlockTypeMail> BASE = REGISTRY.blockGroup(
            BlockMail::new,
            BlockTypeMail.VALUES
    ).item(ItemBlockForestry::new).create();

    private MailBlocks() {
    }
}

package forestry.mail.compat;

import forestry.core.config.Constants;
import forestry.core.utils.JeiUtil;
import forestry.mail.blocks.BlockTypeMail;
import forestry.mail.features.MailBlocks;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@JeiPlugin
@OnlyIn(Dist.CLIENT)
public class MailJeiPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Constants.MOD_ID);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (!ModuleHelper.isEnabled(ForestryModuleUids.MAIL)) {
            return;
        }

        JeiUtil.addDescription(
                registration,
                MailBlocks.BASE.get(BlockTypeMail.MAILBOX).getBlock(),
                MailBlocks.BASE.get(BlockTypeMail.PHILATELIST).getBlock(),
                MailBlocks.BASE.get(BlockTypeMail.TRADE_STATION).getBlock()
        );
    }
}

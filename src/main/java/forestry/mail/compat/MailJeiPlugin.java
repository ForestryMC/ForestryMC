package forestry.mail.compat;

import net.minecraft.util.ResourceLocation;

import forestry.api.core.ForestryAPI;
import forestry.core.config.Constants;
import forestry.core.utils.JeiUtil;
import forestry.mail.ModuleMail;
import forestry.mail.blocks.BlockRegistryMail;
import forestry.modules.ForestryModuleUids;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;

@JEIPlugin
public class MailJeiPlugin implements IModPlugin {
	@Override
	public void register(IModRegistry registry) {
		if (!ForestryAPI.enabledModules.contains(new ResourceLocation(Constants.MOD_ID, ForestryModuleUids.MAIL))) {
			return;
		}

		BlockRegistryMail blocks = ModuleMail.getBlocks();
		JeiUtil.addDescription(registry,
				blocks.mailbox,
				blocks.stampCollector,
				blocks.tradeStation
		);
	}
}

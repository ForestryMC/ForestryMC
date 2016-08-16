package forestry.mail.compat;

import javax.annotation.Nonnull;

import forestry.core.utils.JeiUtil;
import forestry.mail.PluginMail;
import forestry.mail.blocks.BlockRegistryMail;
import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;

@JEIPlugin
public class MailJeiPlugin extends BlankModPlugin {
	@Override
	public void register(@Nonnull IModRegistry registry) {
		BlockRegistryMail blocks = PluginMail.blocks;
		JeiUtil.addDescription(registry,
				blocks.mailbox,
				blocks.stampCollector,
				blocks.tradeStation
		);
	}
}

//package forestry.mail.compat;
//
//import forestry.core.utils.JeiUtil;
//import forestry.mail.ModuleMail;
//import forestry.mail.blocks.BlockRegistryMail;
//import forestry.modules.ForestryModuleUids;
//import forestry.modules.ModuleHelper;
//
//import mezz.jei.api.IModPlugin;
//import mezz.jei.api.IModRegistry;
//import mezz.jei.api.JEIPlugin;
//
//@JEIPlugin
//public class MailJeiPlugin implements IModPlugin {
//	@Override
//	public void register(IModRegistry registry) {
//		if (!ModuleHelper.isEnabled(ForestryModuleUids.MAIL)) {
//			return;
//		}
////TODO JEI
//		BlockRegistryMail blocks = ModuleMail.getBlocks();
//		JeiUtil.addDescription(registry,
//			blocks.mailbox,
//			blocks.stampCollector,
//			blocks.tradeStation
//		);
//	}
//}

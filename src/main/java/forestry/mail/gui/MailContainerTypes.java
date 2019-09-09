package forestry.mail.gui;

import net.minecraft.inventory.container.ContainerType;

import net.minecraftforge.registries.IForgeRegistry;

import forestry.core.gui.ContainerTypes;

public class MailContainerTypes extends ContainerTypes {

	public final ContainerType<ContainerCatalogue> CATALOGUE;
	public final ContainerType<ContainerLetter> LETTER;
	public final ContainerType<ContainerMailbox> MAILBOX;
	public final ContainerType<ContainerStampCollector> STAMP_COLLECTOR;
	public final ContainerType<ContainerTradeName> TRADE_NAME;
	public final ContainerType<ContainerTrader> TRADER;

	public MailContainerTypes(IForgeRegistry<ContainerType<?>> registry) {
		super(registry);

		CATALOGUE = register(ContainerCatalogue::fromNetwork, "catalogue");
		LETTER = register(ContainerLetter::fromNetwork, "letter");
		MAILBOX = register(ContainerMailbox::fromNetwork, "mailbox");
		STAMP_COLLECTOR = register(ContainerStampCollector::fromNetwork, "stamp_collector");
		TRADE_NAME = register(ContainerTradeName::fromNetwork, "trade_name");
		TRADER = register(ContainerTrader::fromNetwork, "trader");
	}
}

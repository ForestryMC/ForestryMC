package forestry.mail.tiles;

import net.minecraft.tileentity.TileEntityType;

import forestry.core.tiles.TileRegistry;
import forestry.mail.ModuleMail;
import forestry.mail.blocks.BlockRegistryMail;

public class TileRegistryMail extends TileRegistry {

	public final TileEntityType<TileMailbox> MAILBOX;
	public final TileEntityType<TileStampCollector> STAMP_COLLECTOR;
	public final TileEntityType<TileTrader> TRADER;

	public TileRegistryMail() {
		BlockRegistryMail blocks = ModuleMail.getBlocks();
		MAILBOX = registerTileEntityType(TileMailbox::new, "mailbox", blocks.mailbox);
		STAMP_COLLECTOR = registerTileEntityType(TileStampCollector::new, "stamp_collector", blocks.stampCollector);
		TRADER = registerTileEntityType(TileTrader::new, "trader", blocks.tradeStation);
	}
}

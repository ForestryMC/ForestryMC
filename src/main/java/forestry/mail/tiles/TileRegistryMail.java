package forestry.mail.tiles;

import net.minecraft.tileentity.TileEntityType;

import forestry.core.tiles.TileRegistry;
import forestry.mail.blocks.BlockTypeMail;
import forestry.mail.features.MailBlocks;

public class TileRegistryMail extends TileRegistry {

	public final TileEntityType<TileMailbox> MAILBOX;
	public final TileEntityType<TileStampCollector> STAMP_COLLECTOR;
	public final TileEntityType<TileTrader> TRADER;

	public TileRegistryMail() {
		MAILBOX = registerTileEntityType(TileMailbox::new, "mailbox", MailBlocks.BASE.get(BlockTypeMail.MAILBOX).block());
		STAMP_COLLECTOR = registerTileEntityType(TileStampCollector::new, "stamp_collector", MailBlocks.BASE.get(BlockTypeMail.PHILATELIST).block());
		TRADER = registerTileEntityType(TileTrader::new, "trader", MailBlocks.BASE.get(BlockTypeMail.TRADE_STATION).block());
	}
}

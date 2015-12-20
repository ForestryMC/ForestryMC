package forestry.apiculture.blocks;

import java.util.Locale;

import org.apache.commons.lang3.text.WordUtils;

import forestry.api.core.Tabs;
import forestry.apiculture.tiles.TileApiaristChest;
import forestry.apiculture.blocks.BlockApicultureChest.BlockApicultureChestType;
import forestry.core.blocks.BlockBase;
import forestry.core.blocks.IMachineProperties;
import forestry.core.tiles.TileForestry;
import net.minecraft.util.IStringSerializable;

public class BlockApicultureChest extends BlockBase<BlockApicultureChestType, BlockApicultureChestType> {
	public BlockApicultureChest() {
		super(true, BlockApicultureChestType.class);
		setCreativeTab(Tabs.tabApiculture);
		setHarvestLevel("axe", 0);
	}
	
	public static enum BlockApicultureChestType implements IMachineProperties, IStringSerializable {
		CHEST(TileApiaristChest.class);

		public static final BlockApicultureChestType[] VALUES = values();

		private final String teIdent;
		private final Class<? extends TileForestry> teClass;

		BlockApicultureChestType(Class<? extends TileForestry> teClass) {
			String name = toString().toLowerCase(Locale.ENGLISH);
			this.teIdent = "forestry." + WordUtils.capitalize(name);
			this.teClass = teClass;
		}

		BlockApicultureChestType(Class<? extends TileForestry> teClass, String name) {
			this.teIdent = "forestry." + name;
			this.teClass = teClass;
		}

		@Override
		public int getMeta() {
			return ordinal();
		}

		@Override
		public String getTeIdent() {
			return teIdent;
		}

		@Override
		public Class<? extends TileForestry> getTeClass() {
			return teClass;
		}
		
		@Override
		public String getName() {
			return toString().toLowerCase(Locale.ENGLISH);
		}
	}
}

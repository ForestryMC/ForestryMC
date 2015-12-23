package forestry.apiculture.blocks;

import java.util.Locale;

import org.apache.commons.lang3.text.WordUtils;

import forestry.apiculture.tiles.TileApiaristChest;
import forestry.core.blocks.IMachinePropertiesTESR;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.TileForestry;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.IStringSerializable;

public enum BlockApicultureChestType implements IMachinePropertiesTESR, IStringSerializable{
	CHEST(TileApiaristChest.class) {
		@Override
		public TileEntitySpecialRenderer getRenderer() {
			return Proxies.render.getRenderChest("apiaristchest");
		}
	};

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

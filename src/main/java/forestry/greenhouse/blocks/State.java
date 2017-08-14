package forestry.greenhouse.blocks;

import java.util.Locale;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;

public enum State implements IStringSerializable {
	ON, OFF;

	public static final PropertyEnum<State> PROPERTY = PropertyEnum.create("state", State.class);

	@Override
	public String getName() {
		return name().toLowerCase(Locale.ENGLISH);
	}
}

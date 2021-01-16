package forestry.arboriculture.capabilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import forestry.api.core.IArmorNaturalist;

public class ArmorNaturalist implements IArmorNaturalist {
	public static final ArmorNaturalist INSTANCE = new ArmorNaturalist();

	protected ArmorNaturalist() {

	}

	@Override
	public boolean canSeePollination(PlayerEntity player, ItemStack armor, boolean doSee) {
		return true;
	}
}

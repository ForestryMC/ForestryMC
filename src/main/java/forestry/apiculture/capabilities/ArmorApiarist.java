package forestry.apiculture.capabilities;

import javax.annotation.Nullable;

import forestry.api.apiculture.IArmorApiarist;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class ArmorApiarist implements IArmorApiarist {
	public static final ArmorApiarist INSTANCE = new ArmorApiarist();

	protected ArmorApiarist() {

	}

	@Override
	public boolean protectEntity(EntityLivingBase entity, ItemStack armor, @Nullable String cause, boolean doProtect) {
		return true;
	}
}

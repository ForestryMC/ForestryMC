package forestry.apiculture.capabilities;

import javax.annotation.Nullable;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import forestry.api.apiculture.IArmorApiarist;

public class ArmorApiarist implements IArmorApiarist {
	public static final ArmorApiarist INSTANCE = new ArmorApiarist();

	protected ArmorApiarist() {

	}

	@Override
	public boolean protectEntity(LivingEntity entity, ItemStack armor, @Nullable ResourceLocation cause, boolean doProtect) {
		return true;
	}
}

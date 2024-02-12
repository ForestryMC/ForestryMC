package forestry.core.items;

import deleteme.Todos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;

public interface HasRemnants {

	ItemStack getRemnants();

	default void todo() {
		// mixin ItemStack#hurtAndBreak
		Todos.todo();
	}

	class Pickaxe extends PickaxeItem implements HasRemnants {

		private final ItemStack remnants;

		public Pickaxe(Tier tier, int damageBonus, float speedModifier, Properties properties, ItemStack remnants) {
			super(tier, damageBonus, speedModifier, properties);
			this.remnants = remnants;
		}

		@Override
		public ItemStack getRemnants() {
			return remnants;
		}
	}

	class Shovel extends ShovelItem implements HasRemnants {

		private final ItemStack remnants;

		public Shovel(Tier tier, float damageBonus, float speedModifier, Properties properties, ItemStack remnants) {
			super(tier, damageBonus, speedModifier, properties);
			this.remnants = remnants;
		}

		@Override
		public ItemStack getRemnants() {
			return remnants;
		}
	}
}

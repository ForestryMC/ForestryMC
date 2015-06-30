package forestry.core.inventory;

import com.google.common.collect.ImmutableSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import forestry.api.core.IErrorSource;
import forestry.api.core.IErrorState;
import forestry.core.EnumErrorCode;
import forestry.core.config.ForestryItem;

public abstract class AlyzerInventory extends ItemInventory implements IErrorSource {

	public static final int SLOT_SPECIMEN = 0;
	public static final int SLOT_ANALYZE_1 = 1;
	public static final int SLOT_ANALYZE_2 = 2;
	public static final int SLOT_ANALYZE_3 = 3;
	public static final int SLOT_ANALYZE_4 = 4;
	public static final int SLOT_ANALYZE_5 = 6;
	public static final int SLOT_ENERGY = 5;

	protected final EntityPlayer player;

	public AlyzerInventory(EntityPlayer player, int size, ItemStack itemstack) {
		super(player, size, itemstack);
		this.player = player;
	}

	protected static boolean isEnergy(ItemStack itemstack) {
		if (itemstack == null || itemstack.stackSize <= 0) {
			return false;
		}

		return ForestryItem.honeyDrop.isItemEqual(itemstack) || ForestryItem.honeydew.isItemEqual(itemstack);
	}

	protected boolean hasSpecimen() {
		for (int i = SLOT_SPECIMEN; i <= SLOT_ANALYZE_5; i++) {
			if (i == SLOT_ENERGY) {
				continue;
			}

			ItemStack itemStack = getStackInSlot(i);
			if (itemStack != null) {
				return true;
			}
		}
		return false;
	}

	protected abstract boolean isSpecimen(ItemStack itemStack);

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		if (slotIndex == SLOT_ENERGY) {
			return isEnergy(itemStack);
		} else {
			return !hasSpecimen() && isSpecimen(itemStack);
		}
	}

	@Override
	public final ImmutableSet<IErrorState> getErrorStates() {
		ImmutableSet.Builder<IErrorState> errorStates = ImmutableSet.builder();

		if (!hasSpecimen()) {
			errorStates.add(EnumErrorCode.NOTHINGANALYZE);
		}

		if (!isEnergy(getStackInSlot(SLOT_ENERGY))) {
			errorStates.add(EnumErrorCode.NOHONEY);
		}

		return errorStates.build();
	}
}

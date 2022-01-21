package forestry.cultivation.inventory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.IFarmable;
import forestry.cultivation.tiles.TilePlanter;
import forestry.farming.FarmHelper;
import forestry.farming.multiblock.IFarmInventoryInternal;
import forestry.farming.multiblock.InventoryPlantation;

public class InventoryPlanter extends InventoryPlantation<TilePlanter> implements IFarmInventoryInternal {
	public static InventoryPlantation.InventoryConfig CONFIG = new InventoryPlantation.InventoryConfig(
			0, 4,
			4, 4,
			8, 4,
			12, 1,
			13, 1
	);

	public InventoryPlanter(TilePlanter housing) {
		super(housing, CONFIG);
	}

	@Override
	public boolean plantGermling(IFarmable germling, Player player, BlockPos pos) {
		for (FarmDirection direction : FarmDirection.values()) {
			if (plantGermling(germling, player, pos, direction)) {
				return true;
			}
		}
		return false;
	}

	public boolean plantGermling(IFarmable germling, Player player, BlockPos pos, FarmDirection direction) {
		int index = FarmHelper.getReversedLayoutDirection(direction).ordinal();
		ItemStack germlingStack = germlingsInventory.getItem(index);
		if (germlingStack.isEmpty() || !germling.isGermling(germlingStack)) {
			return false;
		}

		if (germling.plantSaplingAt(player, germlingStack, player.level, pos)) {
			germlingsInventory.removeItem(index, 1);
			return true;
		}
		return false;
	}
}

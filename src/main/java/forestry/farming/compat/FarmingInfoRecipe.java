package forestry.farming.compat;

import net.minecraft.world.item.ItemStack;

import forestry.api.circuits.ICircuit;
import forestry.api.farming.IFarmProperties;

public record FarmingInfoRecipe(ItemStack tube,
								IFarmProperties properties,
								ICircuit circuit) {


}

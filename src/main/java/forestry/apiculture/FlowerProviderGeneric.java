package forestry.apiculture;

import java.util.EnumSet;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import forestry.api.apiculture.FlowerManager;
import forestry.api.genetics.IFlowerProvider;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IPollinatable;
import forestry.core.utils.StringUtil;

public class FlowerProviderGeneric implements IFlowerProvider {

	private final String flowerType;
	private final String description;

	public FlowerProviderGeneric(String flowerType, String description) {
		this.flowerType = flowerType;
		this.description = description;
	}

	@Override
	public boolean isAcceptedFlower(World world, IIndividual individual, int x, int y, int z) {
		return FlowerRegistry.getInstance().isAcceptedFlower(this.flowerType, world, individual, x, y, z);
	}

	@Override
	public boolean isAcceptedPollinatable(World world, IPollinatable pollinatable) {
		EnumSet<EnumPlantType> types = pollinatable.getPlantType();
		return (types.size() > 1)
				&& (flowerType != FlowerManager.FlowerTypeNether && !types.contains(EnumPlantType.Nether) || flowerType == FlowerManager.FlowerTypeNether && types.contains(EnumPlantType.Nether));
	}

	@Override
	public boolean growFlower(World world, IIndividual individual, int x, int y, int z) {
		return FlowerRegistry.getInstance().growFlower(this.flowerType, world, individual, x, y, z);
	}

	@Override
	public String getDescription() {
		return StringUtil.localize(this.description);
	}

	@Override
	public ItemStack[] affectProducts(World world, IIndividual individual, int x, int y, int z, ItemStack[] products) {
		return products;
	}

	@Override
	public ItemStack[] getItemStacks() {
		return FlowerRegistry.getInstance().getAcceptableFlowers(this.flowerType);
	}

}

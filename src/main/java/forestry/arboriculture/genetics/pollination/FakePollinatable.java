package forestry.arboriculture.genetics.pollination;

import java.util.EnumSet;

import net.minecraftforge.common.EnumPlantType;

import forestry.api.arboriculture.ITree;
import forestry.api.genetics.IPollinatable;

public class FakePollinatable implements IPollinatable {

	private final ICheckPollinatable checkPollinatable;

	public FakePollinatable(ICheckPollinatable checkPollinatable) {
		this.checkPollinatable = checkPollinatable;
	}

	@Override
	public EnumSet<EnumPlantType> getPlantType() {
		return checkPollinatable.getPlantType();
	}

	@Override
	public ITree getPollen() {
		return checkPollinatable.getPollen();
	}

	@Override
	public boolean canMateWith(ITree pollen) {
		return checkPollinatable.canMateWith(pollen);
	}

	@Override
	public void mateWith(ITree pollen) {

	}

	@Override
	public boolean isPollinated() {
		return checkPollinatable.isPollinated();
	}
}

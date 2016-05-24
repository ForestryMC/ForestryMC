package forestry.arboriculture.multiblock;

import forestry.core.multiblock.FakeMultiblockController;
import net.minecraft.util.math.BlockPos;

public class FakeCharcoalPileController extends FakeMultiblockController implements ICharcoalPileControllerInternal {

	public static final FakeCharcoalPileController instance = new FakeCharcoalPileController();
	
	private FakeCharcoalPileController() {
	}
	
	@Override
	public String getUnlocalizedType() {
		return "for.multiblock.charcoalpile.type";
	}

	@Override
	public boolean isActive() {
		return false;
	}
	
	@Override
	public void setActive(boolean active) {
	}

	@Override
	public BlockPos getMinimumCoord() {
		return null;
	}

	@Override
	public BlockPos getMaximumCoord() {
		return null;
	}

	@Override
	public BlockPos getCoordinates() {
		return null;
	}

}

package forestry.arboriculture.charcoal;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

import forestry.api.arboriculture.ICharcoalManager;
import forestry.api.arboriculture.ICharcoalPileWall;
import forestry.core.config.Config;

public class CharcoalManager implements ICharcoalManager {
	private final List<ICharcoalPileWall> walls = new ArrayList<>();

	@Override
	public void registerWall(Block block, int amount) {
		Preconditions.checkNotNull(block, "block must not be null.");
		Preconditions.checkArgument(amount > (-Config.charcoalAmountBase) && amount < (63 - Config.charcoalAmountBase), "amount must be bigger than -10 and smaller than 64.");
		walls.add(new CharcoalPileWall(block, amount));
	}

	@Override
	public void registerWall(BlockState blockState, int amount) {
		Preconditions.checkNotNull(blockState, "block state must not be null.");
		Preconditions.checkArgument(amount > (-Config.charcoalAmountBase) && amount < (63 - Config.charcoalAmountBase), "amount must be bigger than -10 and smaller than 64.");
		walls.add(new CharcoalPileWall(blockState, amount));
	}

	@Override
	public void registerWall(ICharcoalPileWall wall) {
		walls.add(wall);
	}

	@Override
	public boolean removeWall(Block block) {
		return removeWall(block.getDefaultState());
	}

	@Override
	public boolean removeWall(BlockState state) {
		for (ICharcoalPileWall wall : walls) {
			if (wall.matches(state)) {
				return walls.remove(wall);
			}
		}
		return false;
	}

	@Override
	public Collection<ICharcoalPileWall> getWalls() {
		return walls;
	}
}

package forestry.core.utils;

import java.util.AbstractSet;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

/**
 * A set with fast {@link #contains(Object)} for BlockState
 */
public class BlockStateSet extends AbstractSet<BlockState> {
	private final Set<BlockState> blockStates = new HashSet<>();
	private final Map<Block, Integer> blocks = new IdentityHashMap<>();

	@Override
	public boolean add(BlockState blockState) {
		Block block = blockState.getBlock();
		Integer count = blocks.getOrDefault(block, 0);
		blocks.put(block, count + 1);
		return blockStates.add(blockState);
	}

	@Override
	public boolean remove(Object o) {
		BlockState blockState = (BlockState) o;
		Block block = blockState.getBlock();
		Integer count = blocks.getOrDefault(block, 0);
		if (count > 0) {
			blocks.put(block, count - 1);
		} else {
			blocks.remove(block);
		}
		return blockStates.remove(blockState);
	}

	@Override
	public boolean contains(Object o) {
		BlockState blockState = (BlockState) o;
		Block block = blockState.getBlock();
		return blocks.containsKey(block) && blockStates.contains(blockState);
	}

	@Override
	public void clear() {
		blockStates.clear();
		blocks.clear();
	}

	@Override
	public Iterator<BlockState> iterator() {
		return blockStates.iterator();
	}

	@Override
	public int size() {
		return blockStates.size();
	}
}

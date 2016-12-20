package forestry.greenhouse.multiblock;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;

import forestry.api.greenhouse.IInternalBlock;
import forestry.api.greenhouse.IInternalBlockFace;
import net.minecraft.util.math.BlockPos;

/**
 * Used to check if a blockPos is in a collection of IInternalBlock
 */
public class InternalBlockCheck implements IInternalBlock {
	private final BlockPos pos;

	public InternalBlockCheck(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public BlockPos getPos() {
		return pos;
	}

	@Nullable
	@Override
	public IInternalBlock getRoot() {
		return null;
	}

	@Override
	public Collection<IInternalBlockFace> getFaces() {
		return Collections.emptyList();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IInternalBlock)) {
			return false;
		}
		IInternalBlock internalBlock = (IInternalBlock) obj;
		return internalBlock.getPos().equals(getPos());
	}

	@Override
	public int hashCode() {
		return pos.hashCode();
	}
}

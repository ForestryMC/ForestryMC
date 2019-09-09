//package forestry.modules.features;
//
//import javax.annotation.Nullable;
//
//import net.minecraft.block.Block;
//
//import forestry.core.blocks.BlockBase;
//import forestry.core.items.ItemBlockBase;
//
//import binnie.core.machines.BlockMachine;
//import binnie.core.machines.ItemMachine;
//import binnie.core.machines.MachineGroup;
//
//public interface IMachineFeature extends IModFeature<MachineGroup>, IBlockProvider<BlockBase, ItemBlockBase> {
//	@Nullable
//	MachineGroup getGroup();
//
//	default MachineGroup apply(MachineGroup group) {
//		return group;
//	}
//
//	void setGroup(MachineGroup group);
//
//	default MachineGroup group() {
//		MachineGroup group = getGroup();
//		if (group == null) {
//			throw new IllegalStateException("Called feature getter method before content creation.");
//		}
//		return group;
//	}
//
//	boolean hasGroup();
//
//	@Override
//	default boolean hasBlock() {
//		return hasGroup() && group().hasBlock();
//	}
//
//	@Nullable
//	@Override
//	default BlockMachine getBlock() {
//		return hasGroup() ? group().getBlock() : null;
//	}
//
//	@Override
//	default Block block() {
//		if (hasGroup()) {
//			return group().block();
//		} else {
//			throw new IllegalStateException("Called feature getter method before content creation.");
//		}
//	}
//
//	@Override
//	default boolean hasItem() {
//		return hasGroup() && group().hasBlock();
//	}
//
//	@Nullable
//	@Override
//	default ItemMachine getItem() {
//		return hasGroup() ? group().getItem() : null;
//	}
//
//	@Override
//	default ItemMachine item() {
//		if (hasGroup()) {
//			return group().item();
//		} else {
//			throw new IllegalStateException("Called feature getter method before content creation.");
//		}
//	}
//}

package forestry.arboriculture.tiles;

import javax.annotation.Nullable;

import forestry.api.arboriculture.EnumPileType;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.multiblock.ICharcoalPileComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.arboriculture.PluginArboriculture;
import forestry.arboriculture.blocks.BlockPile;
import forestry.arboriculture.genetics.Tree;
import forestry.arboriculture.multiblock.EnumPilePosition;
import forestry.arboriculture.multiblock.MultiblockLogicCharcoalPile;
import forestry.core.multiblock.MultiblockTileEntityForestry;
import forestry.core.tiles.TileUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TilePile extends MultiblockTileEntityForestry<MultiblockLogicCharcoalPile> implements ICharcoalPileComponent<MultiblockLogicCharcoalPile> {
	@Nullable
	private IAlleleTreeSpecies treeSpecies;
	@SideOnly(Side.CLIENT)
	@Nullable
	private BlockPos woodPos;

	public TilePile() {
		super(new MultiblockLogicCharcoalPile());
	}

	@Override
	public void onMachineAssembled(IMultiblockController multiblockController, BlockPos minCoord, BlockPos maxCoord) {
		// Re-render this block on the client
		if (world.isRemote) {
			this.world.markBlockRangeForRenderUpdate(getPos(), getPos());
		}
		world.notifyNeighborsOfStateChange(getPos(), getBlockType(), false);

		if (getPileType() != EnumPileType.ASH) {
			this.world.setBlockState(getPos(), updateState(), 2);
		}
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		Block newBlock = newState.getBlock();
		return oldState.getBlock() != newBlock && newBlock != PluginArboriculture.getBlocks().piles.get(EnumPileType.ASH);
	}

	@Override
	public void onMachineBroken() {
		// Re-render this block on the client
		if (world.isRemote) {
			this.world.markBlockRangeForRenderUpdate(getPos(), getPos());
			woodPos = null;
		}
		world.notifyNeighborsOfStateChange(getPos(), getBlockType(), false);
		markDirty();
		if (getPileType() != EnumPileType.ASH) {
			this.world.setBlockState(getPos(), updateState(), 2);
		}
	}

	private IBlockState updateState() {
		TilePile pile = TileUtil.getTile(world, pos, TilePile.class);
		EnumPilePosition pileType = EnumPilePosition.INTERIOR;
		if (pile != null && pile.getMultiblockLogic().isConnected() && pile.getMultiblockLogic().getController().isAssembled()) {
			BlockPos maxCoord = pile.getMultiblockLogic().getController().getMaximumCoord();
			BlockPos minCoord = pile.getMultiblockLogic().getController().getMinimumCoord();
			int level = pos.getY() - minCoord.getY();

			int levelMinX = minCoord.getX() + level;
			int levelMinY = minCoord.getY() + level;
			int levelMinZ = minCoord.getZ() + level;
			int levelMaxX = maxCoord.getX() - level;
			int levelMaxY = maxCoord.getY() - level;
			int levelMaxZ = maxCoord.getZ() - level;
			int facesMatching = 0;
			if (levelMaxX == pos.getX() || levelMinX == pos.getX()) {
				facesMatching++;
			}
			if (levelMaxZ == pos.getZ() || levelMinZ == pos.getZ()) {
				facesMatching++;
			}

			if (facesMatching > 0) {
				if (levelMaxX == pos.getX()) {
					if (levelMaxZ == pos.getZ()) {
						pileType = EnumPilePosition.CORNER_BACK_RIGHT;
					} else if (levelMinZ == pos.getZ()) {
						pileType = EnumPilePosition.CORNER_FRONT_RIGHT;
					} else {
						pileType = EnumPilePosition.SIDE_RIGHT;
					}
				} else if (levelMinX == pos.getX()) {
					if (levelMaxZ == pos.getZ()) {
						pileType = EnumPilePosition.CORNER_BACK_LEFT;
					} else if (levelMinZ == pos.getZ()) {
						pileType = EnumPilePosition.CORNER_FRONT_LEFT;
					} else {
						pileType = EnumPilePosition.SIDE_LEFT;
					}
				} else if (levelMaxZ == pos.getZ()) {
					if (levelMaxX == pos.getX()) {
						pileType = EnumPilePosition.CORNER_FRONT_RIGHT;
					} else if (levelMinX == pos.getX()) {
						pileType = EnumPilePosition.CORNER_FRONT_LEFT;
					} else {
						pileType = EnumPilePosition.FRONT;
					}
				} else if (levelMinZ == pos.getZ()) {
					if (levelMaxX == pos.getX()) {
						pileType = EnumPilePosition.CORNER_BACK_RIGHT;
					} else if (levelMinX == pos.getX()) {
						pileType = EnumPilePosition.CORNER_BACK_LEFT;
					} else {
						pileType = EnumPilePosition.BACK;
					}
				}
			}
		}
		return getBlockType().getDefaultState().withProperty(BlockPile.PILE_POSITION, pileType);
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);

		// legacy
		if (data.hasKey("ContainedTree")) {
			Tree containedTree = new Tree(data.getCompoundTag("ContainedTree"));
			setTreeSpecies(containedTree.getGenome().getPrimary());
		}

		if (data.hasKey("TreeSpecies")) {
			String treeSpeciesUid = data.getString("TreeSpecies");
			IAllele allele = AlleleManager.alleleRegistry.getAllele(treeSpeciesUid);
			if (allele instanceof IAlleleTreeSpecies) {
				IAlleleTreeSpecies treeSpecies = (IAlleleTreeSpecies) allele;
				setTreeSpecies(treeSpecies);
			}
		}
	}


	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		data = super.writeToNBT(data);
		if (treeSpecies != null) {
			data.setString("TreeSpecies", treeSpecies.getUID());
		}
		return data;
	}

	@Override
	protected void encodeDescriptionPacket(NBTTagCompound packetData) {
		super.encodeDescriptionPacket(packetData);
		if (treeSpecies != null) {
			packetData.setString("TreeSpecies", treeSpecies.getUID());
		}
	}

	@Override
	protected void decodeDescriptionPacket(NBTTagCompound packetData) {
		super.decodeDescriptionPacket(packetData);
		if (packetData.hasKey("TreeSpecies")) {
			String treeSpeciesUid = packetData.getString("TreeSpecies");
			IAllele allele = AlleleManager.alleleRegistry.getAllele(treeSpeciesUid);
			if (allele instanceof IAlleleTreeSpecies) {
				IAlleleTreeSpecies treeSpecies = (IAlleleTreeSpecies) allele;
				setTreeSpecies(treeSpecies);
			}
		}
	}
	
	/* CLIENT INFORMATION */

	/* CONTAINED TREE */
	@Override
	public void setTreeSpecies(IAlleleTreeSpecies treeSpecies) {
		this.treeSpecies = treeSpecies;
		if (world != null && world.isRemote) {
			world.markBlockRangeForRenderUpdate(getPos(), getPos());
		}
	}

	@Override
	public IAlleleTreeSpecies getTreeSpecies() {
		return this.treeSpecies;
	}

	@Override
	public EnumPileType getPileType() {
		IBlockState state = world.getBlockState(pos);
		return ((BlockPile) state.getBlock()).getPileType();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IAlleleTreeSpecies getNextWoodPile() {
		if (woodPos == null) {
			woodPos = getNextWoodPilePos();
		}
		TilePile pile = TileUtil.getTile(world, woodPos, TilePile.class);
		if (pile != null) {
			return pile.getTreeSpecies();
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	private BlockPos getNextWoodPilePos() {
		EnumPilePosition pilePos = world.getBlockState(pos).getValue(BlockPile.PILE_POSITION);
		int layer = pos.getY() - getMultiblockLogic().getController().getMinimumCoord().getY();
		IBlockState state;
		Block woodPile = PluginArboriculture.getBlocks().piles.get(EnumPileType.WOOD);
		switch (pilePos) {
			case BACK:
				state = world.getBlockState(pos.add(0, 0, 1));
				if (state.getBlock() != woodPile) {
					return pos.add(0, -1, 0);
				}
				return pos.add(0, 0, 1);
			case FRONT:
				state = world.getBlockState(pos.add(0, 0, -1));
				if (state.getBlock() != woodPile) {
					return pos.add(0, -1, 0);
				}
				return pos.add(0, 0, -1);
			case SIDE_LEFT:
				state = world.getBlockState(pos.add(1, 0, 0));
				if (state.getBlock() != woodPile) {
					return pos.add(0, -1, 0);
				}
				return pos.add(1, 0, 0);
			case SIDE_RIGHT:
				state = world.getBlockState(pos.add(-1, 0, 0));
				if (state.getBlock() != woodPile) {
					return pos.add(0, -1, 0);
				}
				return pos.add(-1, 0, 0);
			case CORNER_FRONT_LEFT:
				state = world.getBlockState(pos.add(1, 0, 1));
				if (state.getBlock() != woodPile) {
					return pos.add(0, -1, 0);
				}
				return pos.add(1, 0, 1);
			case CORNER_FRONT_RIGHT:
				state = world.getBlockState(pos.add(-1, 0, 1));
				if (state.getBlock() != woodPile) {
					return pos.add(0, -1, 0);
				}
				return pos.add(-1, 0, 1);
			case CORNER_BACK_LEFT:
				state = world.getBlockState(pos.add(1, 0, -1));
				if (state.getBlock() != woodPile) {
					return pos.add(0, -1, 0);
				}
				return pos.add(1, 0, -1);
			case CORNER_BACK_RIGHT:
				state = world.getBlockState(pos.add(-1, 0, -1));
				if (state.getBlock() != woodPile) {
					return pos.add(0, -1, 0);
				}
				return pos.add(-1, 0, -1);
			default:
				return pos.add(0, -1, 0);
		}

	}

	@Override
	public Object getGui(EntityPlayer player, int data) {
		return null;
	}

	@Override
	public Object getContainer(EntityPlayer player, int data) {
		return null;
	}
}

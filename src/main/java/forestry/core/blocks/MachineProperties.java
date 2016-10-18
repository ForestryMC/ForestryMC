package forestry.core.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.registry.GameRegistry;

import forestry.api.core.IModelManager;
import forestry.core.tiles.TileForestry;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.ItemStackUtil;

public class MachineProperties<T extends TileForestry> implements IMachineProperties<T> {
	@Nonnull
	private final String name;
	@Nonnull
	private final String teIdent;
	@Nonnull
	private final Class<T> teClass;
	@Nonnull
	private final AxisAlignedBB boundingBox;
	@Nullable
	private Block block;

	public MachineProperties(@Nonnull Class<T> teClass, @Nonnull String name) {
		this("forestry." + name, teClass, name, new AxisAlignedBB(0, 0, 0, 1, 1, 1));
	}

	public MachineProperties(@Nonnull Class<T> teClass, @Nonnull String name, @Nonnull AxisAlignedBB boundingBox) {
		this("forestry." + name, teClass, name, boundingBox);
	}

	private MachineProperties(@Nonnull String teIdent, @Nonnull Class<T> teClass, @Nonnull String name, @Nonnull AxisAlignedBB boundingBox) {
		this.teIdent = teIdent;
		this.teClass = teClass;
		this.name = name;
		this.boundingBox = boundingBox;
	}

	@Override
	public void setBlock(@Nonnull Block block) {
		this.block = block;
	}

	@Nullable
	@Override
	public Block getBlock() {
		return block;
	}

	@Nonnull
	@Override
	public AxisAlignedBB getBoundingBox(@Nonnull BlockPos pos, @Nonnull IBlockState state) {
		return boundingBox;
	}

	@Nonnull
	@Override
	public RayTraceResult collisionRayTrace(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Vec3d startVec, @Nonnull Vec3d endVec) {
		return BlockUtil.collisionRayTrace(pos, startVec, endVec, boundingBox);
	}

	@Override
	public void registerTileEntity() {
		GameRegistry.registerTileEntity(teClass, teIdent);
	}

	@Override
	public void registerModel(@Nonnull Item item, @Nonnull IModelManager manager) {
		String identifier = ItemStackUtil.getItemNameFromRegistry(item).getResourcePath();
		manager.registerItemModel(item, 0, identifier);
	}

	@Nonnull
	@Override
	public TileEntity createTileEntity() {
		try {
			return teClass.getConstructor().newInstance();
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Failed to instantiate tile entity of class " + teClass.getName(), e);
		}
	}

	@Nonnull
	@Override
	public String getTeIdent() {
		return teIdent;
	}

	@Nonnull
	@Override
	public Class<T> getTeClass() {
		return teClass;
	}

	@Nonnull
	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return true;
	}
}

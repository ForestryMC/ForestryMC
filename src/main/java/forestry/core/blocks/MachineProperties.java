package forestry.core.blocks;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IModelManager;
import forestry.core.tiles.TileForestry;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.MigrationHelper;

public class MachineProperties<T extends TileForestry> implements IMachineProperties<T> {
	private final String name;
	private final Class<T> teClass;
	private final AxisAlignedBB boundingBox;
	@Nullable
	private Block block;

	public MachineProperties(Class<T> teClass, String name) {
		this(teClass, name, new AxisAlignedBB(0, 0, 0, 1, 1, 1));
	}

	public MachineProperties(Class<T> teClass, String name, AxisAlignedBB boundingBox) {
		this.teClass = teClass;
		this.name = name;
		this.boundingBox = boundingBox;
	}

	@Override
	public void setBlock(Block block) {
		this.block = block;
	}

	@Nullable
	@Override
	public Block getBlock() {
		return block;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockAccess world, BlockPos pos, IBlockState state) {
		return boundingBox;
	}

	@Override
	@Nullable
	public RayTraceResult collisionRayTrace(World world, BlockPos pos, IBlockState state, Vec3d startVec, Vec3d endVec) {
		return BlockUtil.collisionRayTrace(pos, startVec, endVec, boundingBox);
	}

	@Override
	public void registerTileEntity() {
		TileUtil.registerTile(teClass, name);
		MigrationHelper.addTileRemappingName(name, name);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModel(Item item, IModelManager manager) {
		ResourceLocation itemNameFromRegistry = item.getRegistryName();
		Preconditions.checkNotNull(itemNameFromRegistry, "No registry name for item");
		String identifier = itemNameFromRegistry.getPath();
		manager.registerItemModel(item, 0, identifier);
	}

	@Override
	public TileEntity createTileEntity() {
		try {
			return teClass.getConstructor().newInstance();
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Failed to instantiate tile entity of class " + teClass.getName(), e);
		}
	}

	@Override
	public Class<T> getTeClass() {
		return teClass;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return true;
	}
}

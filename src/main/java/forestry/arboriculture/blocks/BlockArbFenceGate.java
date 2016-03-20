package forestry.arboriculture.blocks;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.Nonnull;

import forestry.api.arboriculture.EnumWoodType;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.api.core.IStateMapperRegister;
import forestry.api.core.Tabs;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.WoodHelper;
import forestry.arboriculture.WoodHelper.WoodMeshDefinition;
import forestry.core.proxy.Proxies;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockArbFenceGate extends BlockDirectional implements IWoodTyped, IItemModelRegister, IStateMapperRegister {
    public static final PropertyBool OPEN = PropertyBool.create("open");
    public static final PropertyBool POWERED = PropertyBool.create("powered");
    public static final PropertyBool IN_WALL = PropertyBool.create("in_wall");
    
	private final boolean fireproof;
	private final EnumWoodType woodType;

    public BlockArbFenceGate(boolean fireproof, EnumWoodType woodType){
        super(Material.wood);
		this.fireproof = fireproof;
		this.woodType = woodType;
		
		setHardness(2.0F);
		setResistance(5.0F);
		setStepSound(soundTypeWood);
        this.setDefaultState(this.blockState.getBaseState().withProperty(OPEN, Boolean.valueOf(false)).withProperty(POWERED, Boolean.valueOf(false)).withProperty(IN_WALL, Boolean.valueOf(false)));
        this.setCreativeTab(Tabs.tabArboriculture);
    }
    
    @Override
    public boolean isFireproof() {
    	return fireproof;
    }

    @Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos){
        EnumFacing.Axis enumfacing$axis = state.getValue(FACING).getAxis();

        if (enumfacing$axis == EnumFacing.Axis.Z && (worldIn.getBlockState(pos.west()).getBlock() == Blocks.cobblestone_wall || worldIn.getBlockState(pos.east()).getBlock() == Blocks.cobblestone_wall) || enumfacing$axis == EnumFacing.Axis.X && (worldIn.getBlockState(pos.north()).getBlock() == Blocks.cobblestone_wall || worldIn.getBlockState(pos.south()).getBlock() == Blocks.cobblestone_wall)){
            state = state.withProperty(IN_WALL, Boolean.valueOf(true));
        }

        return state;
    }

    @Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos){
        return worldIn.getBlockState(pos.down()).getBlock().getMaterial().isSolid() ? super.canPlaceBlockAt(worldIn, pos) : false;
    }

    @Override
	public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state){
        if (state.getValue(OPEN).booleanValue()){
            return null;
        }else{
            EnumFacing.Axis enumfacing$axis = state.getValue(FACING).getAxis();
            return enumfacing$axis == EnumFacing.Axis.Z ? new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ() + 0.375F, pos.getX() + 1, pos.getY() + 1.5F, pos.getZ() + 0.625F) : new AxisAlignedBB(pos.getX() + 0.375F, pos.getY(), pos.getZ(), pos.getX() + 0.625F, pos.getY() + 1.5F, pos.getZ() + 1);
        }
    }

    @Override
	public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos){
        EnumFacing.Axis enumfacing$axis = worldIn.getBlockState(pos).getValue(FACING).getAxis();

        if (enumfacing$axis == EnumFacing.Axis.Z){
            this.setBlockBounds(0.0F, 0.0F, 0.375F, 1.0F, 1.0F, 0.625F);
        }else{
            this.setBlockBounds(0.375F, 0.0F, 0.0F, 0.625F, 1.0F, 1.0F);
        }
    }

    @Override
	public boolean isOpaqueCube(){
        return false;
    }

    @Override
	public boolean isFullCube(){
        return false;
    }

    @Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos){
        return worldIn.getBlockState(pos).getValue(OPEN).booleanValue();
    }

    @Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing()).withProperty(OPEN, Boolean.valueOf(false)).withProperty(POWERED, Boolean.valueOf(false)).withProperty(IN_WALL, Boolean.valueOf(false));
    }

    @Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ){
        if (state.getValue(OPEN).booleanValue()){
            state = state.withProperty(OPEN, Boolean.valueOf(false));
            worldIn.setBlockState(pos, state, 2);
        }else{
            EnumFacing enumfacing = EnumFacing.fromAngle(playerIn.rotationYaw);

            if (state.getValue(FACING) == enumfacing.getOpposite()){
                state = state.withProperty(FACING, enumfacing);
            }

            state = state.withProperty(OPEN, Boolean.valueOf(true));
            worldIn.setBlockState(pos, state, 2);
        }

        worldIn.playAuxSFXAtEntity(playerIn, state.getValue(OPEN).booleanValue() ? 1003 : 1006, pos, 0);
        return true;
    }

    @Override
	public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock){
        if (!worldIn.isRemote){
            boolean flag = worldIn.isBlockPowered(pos);

            if (flag || neighborBlock.canProvidePower()){
                if (flag && !state.getValue(OPEN).booleanValue() && !state.getValue(POWERED).booleanValue()){
                    worldIn.setBlockState(pos, state.withProperty(OPEN, Boolean.valueOf(true)).withProperty(POWERED, Boolean.valueOf(true)), 2);
                    worldIn.playAuxSFXAtEntity((EntityPlayer)null, 1003, pos, 0);
                }else if (!flag && state.getValue(OPEN).booleanValue() && state.getValue(POWERED).booleanValue()){
                    worldIn.setBlockState(pos, state.withProperty(OPEN, Boolean.valueOf(false)).withProperty(POWERED, Boolean.valueOf(false)), 2);
                    worldIn.playAuxSFXAtEntity((EntityPlayer)null, 1006, pos, 0);
                }else if (flag != state.getValue(POWERED).booleanValue()){
                    worldIn.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(flag)), 2);
                }
            }
        }
    }

    @Override
	@SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side){
        return true;
    }

    @Override
	public IBlockState getStateFromMeta(int meta){
        return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta)).withProperty(OPEN, Boolean.valueOf((meta & 4) != 0)).withProperty(POWERED, Boolean.valueOf((meta & 8) != 0));
    }

    @Override
	public int getMetaFromState(IBlockState state){
        int i = 0;
        i = i | state.getValue(FACING).getHorizontalIndex();

        if (state.getValue(POWERED).booleanValue()){
            i |= 8;
        }

        if (state.getValue(OPEN).booleanValue()){
            i |= 4;
        }

        return i;
    }
    
    @Override
	protected BlockState createBlockState(){
        return new BlockState(this, new IProperty[] {FACING, OPEN, POWERED, IN_WALL});
    }
    
	@Override
	public float getBlockHardness(World world, BlockPos pos) {
		IBlockState blockState = world.getBlockState(pos);
		int meta = getMetaFromState(blockState);
		EnumWoodType woodType = getWoodType(meta);
		return woodType.getHardness();
	}

	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
		if (fireproof) {
			return 0;
		}
		return 20;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
		if (fireproof) {
			return 0;
		}
		return 5;
	}
	
	@Nonnull
	@Override
	public String getBlockKind() {
		return "fence_gates";
	}
	
	@Nonnull
	@Override
	public EnumWoodType getWoodType(int meta) {
		return woodType;
	}
	
	@Nonnull
	@Override
	public Collection<EnumWoodType> getWoodTypes() {
		return Collections.singleton(woodType);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerStateMapper() {
		Proxies.render.registerStateMapper(this, new WoodTypeStateMapper(this, null).addPropertyToRemove(POWERED));
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		manager.registerVariant(item, WoodHelper.getResourceLocations(this));
		manager.registerItemModel(item, new WoodMeshDefinition(this));
	}
}
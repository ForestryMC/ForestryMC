package forestry.arboriculture.blocks;

import java.util.Random;

import forestry.api.arboriculture.ICharcoalPileWall;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.api.core.IStateMapperRegister;
import forestry.api.core.Tabs;
import forestry.arboriculture.PluginArboriculture;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockWoodPile extends Block implements IItemModelRegister, IStateMapperRegister {

	public static final PropertyBool IS_ACTIVE = PropertyBool.create("active");
	public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 7);
	public static final int RANDOM_TICK = 40;
	
	public BlockWoodPile() {
		super(Material.WOOD);
		setHardness(1.5f);
		setTickRandomly(true);
		setCreativeTab(Tabs.tabArboriculture);
		setSoundType(SoundType.WOOD);
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, IS_ACTIVE, AGE);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(IS_ACTIVE) ? 8 + state.getValue(AGE) : state.getValue(AGE);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		boolean isActive = meta > 7;
		return getDefaultState().withProperty(IS_ACTIVE, isActive).withProperty(AGE, meta - (isActive ? 8 : 0));
	}
	
	@Override
	public int tickRate(World world) {
		return 40;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isNormalCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}
	
    @Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state){
    	if(!state.getValue(IS_ACTIVE)){
	    	for(EnumFacing facing : EnumFacing.VALUES){
	    		IBlockState facingState = world.getBlockState(pos.offset(facing));
	    		if(facingState.getBlock() == this && facingState.getValue(IS_ACTIVE)){
	    			world.setBlockState(pos, state.withProperty(IS_ACTIVE, true));
	    			break;
	    		}
	    	}
    	}
    	world.scheduleUpdate(pos, this, this.tickRate(world) + world.rand.nextInt(RANDOM_TICK));
    }
	
	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
		IBlockState fromState = world.getBlockState(fromPos);
		boolean isActive = state.getValue(IS_ACTIVE);
		if(fromState.getBlock() == this){
			if(fromState.getValue(IS_ACTIVE) && !isActive){
				world.setBlockState(pos, state.withProperty(IS_ACTIVE, true));
				world.scheduleUpdate(pos, this, this.tickRate(world) + world.rand.nextInt(RANDOM_TICK));
			}
		} else if(fromState.getBlock() == Blocks.FIRE) {
			if(!isActive){
				world.setBlockState(pos, state.withProperty(IS_ACTIVE, true));
				world.scheduleUpdate(pos, this, this.tickRate(world) + world.rand.nextInt(RANDOM_TICK));
			}
		}
	}
	
	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		if(state.getValue(IS_ACTIVE)){
			for(EnumFacing facing : EnumFacing.VALUES){
				if(world.isAirBlock(pos.offset(facing))){
					world.setBlockState(pos.offset(facing), Blocks.FIRE.getDefaultState());
				}
				if(rand.nextInt(150) == 0){
					if(state.getValue(AGE) < 7){
						world.setBlockState(pos, state.withProperty(AGE, state.getValue(AGE) + 1));
					}else{
						world.setBlockState(pos, PluginArboriculture.getBlocks().charcoal.getDefaultState().withProperty(BlockCharcoal.AMOUNT, Math.round(getCharcoalAmount(world, pos))));
					}
				}
				world.scheduleUpdate(pos, this, this.tickRate(world) + world.rand.nextInt(RANDOM_TICK));
			}
		}
	}
	
	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return 12;
	}
	
	@Override
	public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return true;
	}
	
	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return 25;
	}
	
	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		if (state.getValue(IS_ACTIVE)) {
			return 10;
		}
		return super.getLightValue(state, world, pos);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
		if (state.getValue(IS_ACTIVE)) {
	        if (rand.nextInt(24) == 0){
	            world.playSound(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
	        }
			float f = pos.getX() + 0.5F;
			float f1 = pos.getY() + 0.0F + rand.nextFloat() * 6.0F / 16.0F;
			float f2 = pos.getZ() + 0.5F;
			float f3 = 0.52F;
			float f4 = rand.nextFloat() * 0.6F - 0.3F;
	        if(rand.nextInt(12) == 0){
				world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, f + f3 - 0.5, f1 + 1, f2 + f4, 0.0D, 0.0D, 0.0D);
	        }else{
				world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, f + f3 - 0.5, f1 + 1, f2 + f4, 0.0D, 0.05D, 0.0D);
	        }
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, 0);
	}

	public float getCharcoalAmount(World world, BlockPos pos){
		float charcoalAmount = 0F;
		for(EnumFacing facing : EnumFacing.VALUES){
			charcoalAmount += getCharcoalFaceAmount(world, pos, facing);
		}
		return charcoalAmount / 6;
	}
	
	private int getCharcoalFaceAmount(World world, BlockPos pos, EnumFacing facing){
		int faceAmount = 0;
		for(int i = 0;i < 18;i++){
			BlockPos testPos = pos.offset(facing, i);
			IBlockState state = world.getBlockState(testPos);
			if(state.getBlock() == Blocks.AIR){
				faceAmount = 0;
			}else if(state.getBlock() == this || state.getBlock() == PluginArboriculture.getBlocks().charcoal){
				if(i == 17){
					return getCharcoalFaceAmount(world, testPos, facing);
				}
				continue;
			}else{
				for(ICharcoalPileWall wall : TreeManager.pileWalls){
					if(wall.matches(state)){
						faceAmount = wall.getCharcoalAmount();
						break;
					}
				}
			}
			break;
		}
		return Math.max(1, faceAmount);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerStateMapper() {
		ModelLoader.setCustomStateMapper(this, new StateMap.Builder().ignore(AGE, IS_ACTIVE).build());
	}
	
}

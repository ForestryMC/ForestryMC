package forestry.greenhouse.logics;

import forestry.api.greenhouse.DefaultGreenhouseLogic;
import forestry.api.greenhouse.GreenhouseManager;
import forestry.api.greenhouse.IInternalBlock;
import forestry.api.greenhouse.ITerrainRecipe;
import forestry.api.multiblock.IGreenhouseController;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GreenhouseLogicTerrain extends DefaultGreenhouseLogic {

	private static final int TIME = 160;
	
	private IInternalBlock[] internalBlocks;
	private World world;
    private int timeUntilNextTerrain;
	
	public GreenhouseLogicTerrain(IGreenhouseController controller) {
		super(controller, "Terrain");
		this.internalBlocks = new IInternalBlock[0];
		this.world = controller.getWorldObj();
		this.timeUntilNextTerrain = TIME;
	}
	
	@Override
	public void work() {
		if(this.world.isRemote || controller == null || !controller.isAssembled() || internalBlocks.length == 0){
			return;
		}
        if(this.timeUntilNextTerrain > 0){
            this.timeUntilNextTerrain--;
            if(this.timeUntilNextTerrain <= 0){
        		IInternalBlock internalBlock = internalBlocks[world.rand.nextInt(internalBlocks.length)];
        		BlockPos blockPos = internalBlock.getPos();
        		if(world.isBlockLoaded(blockPos) && !world.isAirBlock(blockPos)){
        			IBlockState blockState = world.getBlockState(blockPos);
        			ITerrainRecipe recipe = GreenhouseManager.greenhouseHelper.getValidTerrainRecipe(blockState, controller.getRegion().getPosition(blockPos).getInfo());
        			if(recipe != null){
        				if(recipe.getChance() > world.rand.nextFloat()){
        					world.setBlockState(blockPos, recipe.getResult());
        				}
        			}
        		}
        		timeUntilNextTerrain = TIME;
            }
        }
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("Time", timeUntilNextTerrain);
		return super.writeToNBT(nbt);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		timeUntilNextTerrain = nbt.getInteger("Time");
		if(timeUntilNextTerrain <= 0){
			timeUntilNextTerrain = TIME;
		}
	}
	
	@Override
	public void onMachineAssembled() {
		this.internalBlocks = controller.getInternalBlocks().toArray(new IInternalBlock[0]);
	}
	
	@Override
	public void onMachineDisassembled() {
		this.internalBlocks = new IInternalBlock[0];
	}

}

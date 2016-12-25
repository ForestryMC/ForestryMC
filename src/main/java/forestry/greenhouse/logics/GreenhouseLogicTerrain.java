package forestry.greenhouse.logics;

import java.util.HashSet;
import java.util.Set;

import forestry.api.greenhouse.DefaultGreenhouseLogic;
import forestry.api.greenhouse.IInternalBlock;
import forestry.api.multiblock.IGreenhouseController;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GreenhouseLogicTerrain extends DefaultGreenhouseLogic {

	private World world;
	private Set<BlockPos> terrain;
	
	public GreenhouseLogicTerrain(IGreenhouseController controller) {
		super(controller, "Terrain");
		this.terrain = new HashSet<>();
		this.world = controller.getWorldObj();
	}
	
	@Override
	public void work() {
		if (controller == null || !controller.isAssembled()) {
			return;
		}
	}
	
	@Override
	public void onMachineAssembled() {
		for(IInternalBlock internalBlock : controller.getInternalBlocks()){
			BlockPos pos = internalBlock.getPos();
		}
	}
	
	@Override
	public void onMachineDisassembled() {
		terrain.clear();
	}

}

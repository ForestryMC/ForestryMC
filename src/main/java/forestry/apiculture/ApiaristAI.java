package forestry.apiculture;

import forestry.api.apiculture.EnumBeeType;
import forestry.apiculture.blocks.BlockApiculture;
import forestry.apiculture.items.ItemBeeGE;
import forestry.apiculture.tiles.TileBeeHouse;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.InventoryUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@SuppressWarnings("static-access")
public class ApiaristAI extends EntityAIMoveToBlock{
	private final EntityVillager villager;
	private boolean hasDrone;
	private boolean hasPrincess;
	private InventoryBasic inventory;
	
	public ApiaristAI(EntityVillager villager, double speed) {
		super(villager, speed, 16);
		this.villager = villager;
		inventory = villager.getVillagerInventory();
	}
	
	@Override
	public boolean shouldExecute() {
		if(this.runDelay<0) {
			this.hasDrone=hasBeeType(EnumBeeType.DRONE);
			this.hasPrincess=hasBeeType(EnumBeeType.PRINCESS);
		}
		return super.shouldExecute();
	}
	
	@Override 
	public void updateTask()
	 {
        super.updateTask();
        this.villager.getLookHelper().setLookPosition((double)this.destinationBlock.getX() + 0.5D, (double)(this.destinationBlock.getY() + 1), (double)this.destinationBlock.getZ() + 0.5D, 10.0F, (float)this.villager.getVerticalFaceSpeed());

        if (this.villager.getDistanceSqToCenter(this.destinationBlock) > 4.0D)
        {
            World world = this.villager.world;
            BlockPos blockpos = this.destinationBlock;
            if(!(TileUtil.getTile(world, blockpos) instanceof TileBeeHouse)) {
            	return;
            }
        	TileBeeHouse beeHouse = (TileBeeHouse) TileUtil.getTile(world,blockpos);
			InventoryBeeHousing inventory = (InventoryBeeHousing) beeHouse.getBeeInventory();
            
			//fill slots from inside bee house
        	for(ItemStack stack : InventoryUtil.getStacks(inventory, inventory.SLOT_PRODUCT_1, inventory.SLOT_PRODUCT_COUNT)) {
        		if(stack!=ItemStack.EMPTY && stack.getItem() instanceof ItemBeeGE) {
        			EnumBeeType type = ((ItemBeeGE)stack.getItem()).getType();
        			if(inventory.getStackInSlot(inventory.SLOT_QUEEN).isEmpty() && type==EnumBeeType.PRINCESS) {
        				inventory.setQueen(stack.copy());
						stack.setCount(0);
        			} else if(type==EnumBeeType.DRONE) {
        				InventoryUtil.addStack(inventory, stack, inventory.SLOT_DRONE, 1, true);
        			}
        		}
        	
        	}
			
        	
        	//fill slots from villager inventory
        	if(inventory.getStackInSlot(inventory.SLOT_DRONE).isEmpty() || inventory.getStackInSlot(inventory.SLOT_QUEEN).isEmpty()) {
        		boolean hasPrincess = false;
        		boolean hasDrone = false;
        		for(ItemStack stack : InventoryUtil.getStacks(villager.getVillagerInventory())) {
        			if(hasPrincess && hasDrone) {
        				break;
        			}
        			if(stack!=ItemStack.EMPTY && stack.getItem() instanceof ItemBeeGE) {
            			EnumBeeType type = ((ItemBeeGE)stack.getItem()).getType();
            			if(type==EnumBeeType.DRONE && inventory.getStackInSlot(inventory.SLOT_DRONE).isEmpty()) {
            				InventoryUtil.addStack(inventory, stack, inventory.SLOT_DRONE, 1, true);
            				hasDrone = true;
            			} else if(type==EnumBeeType.PRINCESS && inventory.getStackInSlot(inventory.SLOT_QUEEN).isEmpty()) {
            				InventoryUtil.addStack(inventory, stack, inventory.SLOT_QUEEN, 1, true);
            				hasPrincess = true;
            			}
        			}
        		}
        	}
            
        	//add remaining bees to villager inventory
            for(ItemStack stack : InventoryUtil.getStacks(inventory, inventory.SLOT_PRODUCT_1, inventory.SLOT_PRODUCT_COUNT)) {
            	if(stack.getItem() instanceof ItemBeeGE) {
            		InventoryUtil.addStack(villager.getVillagerInventory(), stack, true);
            	}
            }
        }
            this.runDelay = 20;
    }
	
	public boolean hasBeeType(EnumBeeType type) {
		if(inventory.isEmpty()) {
			return false;
		}
		for(int i=0 ; i<inventory.getSizeInventory();i++) {
			if(!inventory.getStackInSlot(i).isEmpty() && inventory.getStackInSlot(i).getItem() instanceof ItemBeeGE) {
				if(((ItemBeeGE)inventory.getStackInSlot(i).getItem()).getType()==type) {
					return true;
				}
			}
		}
		return false;
	}

	protected boolean shouldMoveTo(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		if(block instanceof BlockApiculture && TileUtil.getTile(world, pos) instanceof TileBeeHouse) {
			TileBeeHouse beeHouse = (TileBeeHouse) TileUtil.getTile(world,pos);
			InventoryBeeHousing inventory = (InventoryBeeHousing) beeHouse.getBeeInventory();
			if(inventory.isEmpty() ) {
				return false;
			}
			if(!inventory.getStackInSlot(inventory.SLOT_QUEEN).isEmpty()) {
				EnumBeeType type = ((ItemBeeGE) inventory.getStackInSlot(inventory.SLOT_QUEEN).getItem()).getType();
				if(type==EnumBeeType.QUEEN) {
					return false;
				}
				if(type==EnumBeeType.PRINCESS && !inventory.getStackInSlot(inventory.SLOT_DRONE).isEmpty() && !hasDrone) {
					return false;
				}
			}
			boolean foundPrincess = hasPrincess;
			boolean foundDrone = hasDrone;
			if(foundDrone && foundPrincess) return true;
			for(ItemStack stack : InventoryUtil.getStacks(inventory, inventory.SLOT_PRODUCT_1, inventory.SLOT_PRODUCT_COUNT)) {
				if(stack!=ItemStack.EMPTY && stack.getItem() instanceof ItemBeeGE) {
					EnumBeeType type = ((ItemBeeGE)stack.getItem()).getType();
					if(type==EnumBeeType.PRINCESS) foundPrincess = true;
					if(type==EnumBeeType.DRONE) foundDrone = true;
					if(foundDrone && foundPrincess) {
						return true;
					}
				}
			}
			return false;
			//maybe use error states instead?
		}
		return false;
	}
	
	
}

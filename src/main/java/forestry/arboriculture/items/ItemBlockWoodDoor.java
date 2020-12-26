package forestry.arboriculture.items;

import forestry.arboriculture.blocks.BlockForestryDoor;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;

//TODO eg    public static final Item OAK_DOOR = register(new TallBlockItem(Blocks.OAK_DOOR, (new Item.Properties()).group(ItemGroup.REDSTONE)));
public class ItemBlockWoodDoor extends ItemBlockWood<BlockForestryDoor> {

    public ItemBlockWoodDoor(BlockForestryDoor block) {
        super(block);
    }

    /**
     * Copy of {@link net.minecraft.item.TallBlockItem#placeBlock(BlockItemUseContext, BlockState)}
     */
    @Override
    protected boolean placeBlock(BlockItemUseContext context, BlockState state) {
        context.getWorld().setBlockState(context.getPos().up(), Blocks.AIR.getDefaultState(), 27);
        return super.placeBlock(context, state);
    }

    @Override
    public int getBurnTime(ItemStack itemStack) {
        if (getBlock().isFireproof()) {
            return 0;
        } else {
            return 200;
        }
    }
}

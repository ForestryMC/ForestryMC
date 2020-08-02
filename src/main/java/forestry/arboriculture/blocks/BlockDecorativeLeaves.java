package forestry.arboriculture.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IForgeShearable;

import genetics.api.individual.IGenome;

import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.ILeafSpriteProvider;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.core.blocks.IColoredBlock;
import forestry.core.proxy.Proxies;
import forestry.core.utils.BlockUtil;

//TODO shearing
public class BlockDecorativeLeaves extends Block implements IColoredBlock, IForgeShearable {
    private final TreeDefinition definition;

    public BlockDecorativeLeaves(TreeDefinition definition) {
        super(Properties.create(Material.LEAVES)
                .hardnessAndResistance(0.2f)
                .sound(SoundType.PLANT)
                .notSolid()
                .setSuffocates(BlockUtil::alwaysTrue)
                .setOpaque((state, reader, pos) -> !Proxies.render.fancyGraphicsEnabled() && !TreeDefinition.Willow.equals(definition))
        );
        //		this.setCreativeTab(Tabs.tabArboriculture);
        //		this.setLightOpacity(1);	//TODO block stuff);
        this.definition = definition;
    }

    public TreeDefinition getDefinition() {
        return definition;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        if (TreeDefinition.Willow.equals(definition)) {
            return VoxelShapes.empty();
        }
        return super.getCollisionShape(state, worldIn, pos, context);
    }

    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        super.onEntityCollision(state, worldIn, pos, entityIn);
        Vector3d motion = entityIn.getMotion();
        entityIn.setMotion(motion.mul(0.4D, 1.0D, 0.4D));
    }

    /* PROPERTIES */
    @Override
    public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        return 60;
    }

    @Override
    public boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        return true;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        if (face == Direction.DOWN) {
            return 20;
        } else if (face != Direction.UP) {
            return 10;
        } else {
            return 5;
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int colorMultiplier(BlockState state, @Nullable IBlockReader worldIn, @Nullable BlockPos pos, int tintIndex) {
        IGenome genome = definition.getGenome();

        if (tintIndex == BlockAbstractLeaves.FRUIT_COLOR_INDEX) {
            IFruitProvider fruitProvider = genome.getActiveAllele(TreeChromosomes.FRUITS).getProvider();
            return fruitProvider.getDecorativeColor();
        }
        ILeafSpriteProvider spriteProvider = genome.getActiveAllele(TreeChromosomes.SPECIES).getLeafSpriteProvider();
        return spriteProvider.getColor(false);
    }
}

package forestry.core.climate;

import forestry.api.climate.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FakeClimateTransformer implements IClimateTransformer {
    public static final FakeClimateTransformer INSTANCE = new FakeClimateTransformer();

    private FakeClimateTransformer() {
    }

    @Override
    public void setCircular(boolean circular) {
    }

    @Override
    public boolean isCircular() {
        return false;
    }

    @Override
    public int getRange() {
        return 0;
    }

    @Override
    public void setRange(int range) {
    }

    @Override
    public void setTarget(IClimateState target) {

    }

    @Override
    public void setCurrent(IClimateState state) {

    }

    @Override
    public int getArea() {
        return 0;
    }

    @Override
    public float getAreaModifier() {
        return 0.0F;
    }

    @Override
    public float getCostModifier() {
        return 0.0F;
    }

    @Override
    public float getSpeedModifier() {
        return 0.0F;
    }

    @Override
    public IClimateState getTarget() {
        return ClimateStateHelper.INSTANCE.absent();
    }

    @Override
    public IClimateState getCurrent() {
        return ClimateStateHelper.INSTANCE.absent();
    }

    @Override
    public IClimateState getDefault() {
        return ClimateStateHelper.INSTANCE.absent();
    }

    @Override
    public void update() {
    }

    @Override
    public void removeTransformer() {
    }

    @Override
    public IClimateHousing getHousing() {
        return null;
    }

    @Override
    public IClimateManipulatorBuilder createManipulator(ClimateType type) {
        return null;
    }

    @Override
    public BlockPos getCoordinates() {
        return BlockPos.ZERO;
    }

    @Override
    public World getWorldObj() {
        return null;
    }
}

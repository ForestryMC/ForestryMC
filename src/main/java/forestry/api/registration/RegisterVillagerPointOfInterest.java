package forestry.api.registration;

import com.google.common.collect.ImmutableSet;
import forestry.core.config.Constants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.coremod.api.ASMAPI;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

public class RegisterVillagerPointOfInterest {
    public static PointOfInterestType create(String name, Collection<BlockState> block) {
        PointOfInterestType type = new PointOfInterestType(
                Constants.MOD_ID + ":" + name,
                ImmutableSet.copyOf(block),
                1,
                1
        );

        try {
            // PointOfInterestType.registerBlockStates(type);
            String functionName = ASMAPI.mapMethod("func_221052_a"); // registerBlockStates
            Method method = PointOfInterestType.class.getDeclaredMethod(functionName, PointOfInterestType.class);
            method.setAccessible(true);
            method.invoke(null, type);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return type;
    }

    public static Collection<BlockState> assembleStates(Block block) {
        return new ArrayList<>(block.getStateContainer().getValidStates());
    }
}

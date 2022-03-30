package forestry.core.registration;

import com.google.common.collect.ImmutableSet;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.ai.village.poi.PoiType;

import net.minecraftforge.coremod.api.ASMAPI;

import forestry.core.config.Constants;

public class RegisterVillagerPointOfInterest {
	public static PoiType create(String name, Collection<BlockState> block) {
		PoiType type = new PoiType(Constants.MOD_ID + ":" + name, ImmutableSet.copyOf(block), 1, 1);

		try {
			// PointOfInterestType.registerBlockStates(type);
			String functionName = ASMAPI.mapMethod("registerBlockStates"); // registerBlockStates
			Method method = PoiType.class.getDeclaredMethod(functionName, PoiType.class);
			method.setAccessible(true);
			method.invoke(null, type);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return type;
	}

	public static Collection<BlockState> assembleStates(Block block) {
		return new ArrayList<>(block.getStateDefinition().getPossibleStates());
	}
}

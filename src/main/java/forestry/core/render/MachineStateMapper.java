package forestry.core.render;

import com.google.common.collect.Maps;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.state.IProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import forestry.core.blocks.BlockBase;
import forestry.core.blocks.IBlockType;
import forestry.core.blocks.IMachinePropertiesTesr;

@OnlyIn(Dist.CLIENT)
public class MachineStateMapper<T extends Enum<T> & IBlockType & IStringSerializable> extends ForestryStateMapper {

	private final T type;
	@Nullable
	private final String directory;

	public MachineStateMapper(T type) {
		this(type, null);
	}

	public MachineStateMapper(T type, @Nullable String directory) {
		this.type = type;
		this.directory = directory;
	}

	//	@Override TODO - statemapper
	public Map<BlockState, ModelResourceLocation> putStateModelLocations(Block block) {
		if (!(type.getMachineProperties() instanceof IMachinePropertiesTesr)) {
			for (Direction facing : Direction.values()) {
				if (facing == Direction.DOWN || facing == Direction.UP) {
					continue;
				}
				BlockState state = block.getDefaultState().with(BlockBase.FACING, facing);
				LinkedHashMap<IProperty<?>, Comparable<?>> linkedhashmap = Maps.newLinkedHashMap(state.getValues());
				ResourceLocation blockLocation = ForgeRegistries.BLOCKS.getKey(block);
				String s = String.format("%s:%s", blockLocation.getNamespace(), (directory != null ? directory : "") + blockLocation.getPath());
				mapStateModelLocations.put(state, new ModelResourceLocation(s, getPropertyString(linkedhashmap)));
			}
		}

		return this.mapStateModelLocations;
	}

}

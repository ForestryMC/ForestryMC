package forestry.core.render;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.collect.Maps;
import forestry.core.blocks.BlockBase;
import forestry.core.blocks.IBlockType;
import forestry.core.blocks.IMachinePropertiesTesr;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MachineStateMapper<T extends Enum<T> & IBlockType & IStringSerializable> extends ForestryStateMapper {

	private final T type;

	public MachineStateMapper(T type) {
		this.type = type;
	}

	@Override
	public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block block) {
		if (!(type.getMachineProperties() instanceof IMachinePropertiesTesr)) {
			for (EnumFacing facing : EnumFacing.values()) {
				if (facing == EnumFacing.DOWN || facing == EnumFacing.UP) {
					continue;
				}
				IBlockState state = block.getDefaultState().withProperty(BlockBase.FACING, facing);
				LinkedHashMap<IProperty<?>, Comparable<?>> linkedhashmap = Maps.newLinkedHashMap(state.getProperties());
				ResourceLocation blockLocation = Block.REGISTRY.getNameForObject(block);
				String s = String.format("%s:%s", blockLocation.getResourceDomain(), blockLocation.getResourcePath());
				mapStateModelLocations.put(state, new ModelResourceLocation(s, getPropertyString(linkedhashmap)));
			}
		}

		return this.mapStateModelLocations;
	}

}

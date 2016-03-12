package forestry.greenhouse;

import java.util.List;
import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import forestry.core.config.Constants;
import forestry.farming.blocks.EnumFarmBlockType;
import forestry.farming.proxy.ProxyFarming;
import forestry.greenhouse.blocks.BlockGreenhouseType;
import forestry.greenhouse.blocks.BlockRegistryGreenhouse;
import forestry.greenhouse.proxy.ProxyGreenhouse;
import forestry.plugins.BlankForestryPlugin;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.ForestryPluginUids;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@ForestryPlugin(pluginID = ForestryPluginUids.GREENHOUSE, name = "Greenhouse", author = "Nedelosk", url = Constants.URL, unlocalizedDescription = "for.plugin.greenhouse.description")
public class PluginGreenhouse extends BlankForestryPlugin {

	@SidedProxy(clientSide = "forestry.greenhouse.proxy.ProxyGreenhouseClient", serverSide = "forestry.greenhouse.proxy.ProxyGreenhouse")
	public static ProxyGreenhouse proxy;
	
	public static BlockRegistryGreenhouse blocks;
	private static final List<ItemStack> greenhouseGlass = Lists.newArrayList();
	
	@Override
	public void registerItemsAndBlocks() {
		blocks = new BlockRegistryGreenhouse();
	}
	
	@Override
	public void preInit(){
		MinecraftForge.EVENT_BUS.register(this);
		
		addGreenhouseGlass(new ItemStack(Blocks.glass, 1, 0));
		addGreenhouseGlass(new ItemStack(Blocks.stained_glass, 1, 0));
		addGreenhouseGlass(new ItemStack(Blocks.stained_glass, 1, 1));
		addGreenhouseGlass(new ItemStack(Blocks.stained_glass, 1, 2));
		addGreenhouseGlass(new ItemStack(Blocks.stained_glass, 1, 3));
		addGreenhouseGlass(new ItemStack(Blocks.stained_glass, 1, 4));
		addGreenhouseGlass(new ItemStack(Blocks.stained_glass, 1, 5));
		addGreenhouseGlass(new ItemStack(Blocks.stained_glass, 1, 6));
		addGreenhouseGlass(new ItemStack(Blocks.stained_glass, 1, 7));
		addGreenhouseGlass(new ItemStack(Blocks.stained_glass, 1, 8));
		addGreenhouseGlass(new ItemStack(Blocks.stained_glass, 1, 9));
		addGreenhouseGlass(new ItemStack(Blocks.stained_glass, 1, 10));
		addGreenhouseGlass(new ItemStack(Blocks.stained_glass, 1, 11));
		addGreenhouseGlass(new ItemStack(Blocks.stained_glass, 1, 12));
		addGreenhouseGlass(new ItemStack(Blocks.stained_glass, 1, 13));
		addGreenhouseGlass(new ItemStack(Blocks.stained_glass, 1, 14));
		addGreenhouseGlass(new ItemStack(Blocks.stained_glass, 1, 15));
		
		proxy.initializeModels();
	}
	
	public static void addGreenhouseGlass(@Nonnull ItemStack stack){
		for(ItemStack glass : greenhouseGlass){
			if(ItemStack.areItemStackTagsEqual(stack, glass)){
				return;
			}
		}
		greenhouseGlass.add(stack);
	}
	
	public static boolean isGreenhouseGlass(@Nonnull ItemStack stack){
		for(ItemStack glass : greenhouseGlass){
			if(ItemStack.areItemStackTagsEqual(stack, glass)){
				return true;
			}
		}
		return false;
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void handleTextureRemap(TextureStitchEvent.Pre event) {
		BlockGreenhouseType.registerSprites();
	}
	
}

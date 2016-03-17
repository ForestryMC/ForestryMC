/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.greenhouse;

import forestry.api.greenhouse.GreenhouseManager;
import forestry.api.greenhouse.IGreenhouseItemAccess;
import forestry.core.config.Constants;
import forestry.core.network.IPacketRegistry;
import forestry.greenhouse.blocks.BlockGreenhouseType;
import forestry.greenhouse.blocks.BlockRegistryGreenhouse;
import forestry.greenhouse.network.PacketRegistryGreenhouse;
import forestry.greenhouse.proxy.ProxyGreenhouse;
import forestry.greenhouse.tiles.TileGreenhouseControl;
import forestry.greenhouse.tiles.TileGreenhouseDoor;
import forestry.greenhouse.tiles.TileGreenhouseDryer;
import forestry.greenhouse.tiles.TileGreenhouseFan;
import forestry.greenhouse.tiles.TileGreenhouseGearbox;
import forestry.greenhouse.tiles.TileGreenhouseHatch;
import forestry.greenhouse.tiles.TileGreenhouseHeater;
import forestry.greenhouse.tiles.TileGreenhousePlain;
import forestry.greenhouse.tiles.TileGreenhouseSprinkler;
import forestry.greenhouse.tiles.TileGreenhouseValve;
import forestry.plugins.BlankForestryPlugin;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.ForestryPluginUids;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@ForestryPlugin(pluginID = ForestryPluginUids.GREENHOUSE, name = "Greenhouse", author = "Nedelosk", url = Constants.URL, unlocalizedDescription = "for.plugin.greenhouse.description")
public class PluginGreenhouse extends BlankForestryPlugin {

	@SidedProxy(clientSide = "forestry.greenhouse.proxy.ProxyGreenhouseClient", serverSide = "forestry.greenhouse.proxy.ProxyGreenhouse")
	public static ProxyGreenhouse proxy;
	
	public static BlockRegistryGreenhouse blocks;
	
	@Override
	public void setupAPI() {
		GreenhouseManager.greenhouseItemAccess = new GreenhouseItemAccess();
	}
	
	@Override
	public void registerItemsAndBlocks() {
		blocks = new BlockRegistryGreenhouse();
	}
	
	@Override
	public void doInit() {
		super.doInit();
		
		GameRegistry.registerTileEntity(TileGreenhouseFan.class, "forestry.GreenhouseFan");
		GameRegistry.registerTileEntity(TileGreenhouseHeater.class, "forestry.GreenhouseHeater");
		GameRegistry.registerTileEntity(TileGreenhouseDryer.class, "forestry.GreenhouseDryer");
		GameRegistry.registerTileEntity(TileGreenhouseSprinkler.class, "forestry.GreenhouseSprinkler");
		GameRegistry.registerTileEntity(TileGreenhouseValve.class, "forestry.GreenhouseValve");
		GameRegistry.registerTileEntity(TileGreenhouseGearbox.class, "forestry.GreenhouseGearbox");
		GameRegistry.registerTileEntity(TileGreenhouseControl.class, "forestry.GreenhouseController");
		GameRegistry.registerTileEntity(TileGreenhousePlain.class, "forestry.GreenhousePlain");
		GameRegistry.registerTileEntity(TileGreenhouseDoor.class, "forestry.GreenhouseDoor");
		GameRegistry.registerTileEntity(TileGreenhouseHatch.class, "forestry.GreenhouseHatch");
	}
	
	@Override
	public void preInit(){
		MinecraftForge.EVENT_BUS.register(this);
		
		IGreenhouseItemAccess greenhouseAccess = GreenhouseManager.greenhouseItemAccess;
		
		greenhouseAccess.registerGreenhouseGlass(new ItemStack(Blocks.glass, 1, 0));
		greenhouseAccess.registerGreenhouseGlass(new ItemStack(Blocks.stained_glass, 1, 0));
		greenhouseAccess.registerGreenhouseGlass(new ItemStack(Blocks.stained_glass, 1, 1));
		greenhouseAccess.registerGreenhouseGlass(new ItemStack(Blocks.stained_glass, 1, 2));
		greenhouseAccess.registerGreenhouseGlass(new ItemStack(Blocks.stained_glass, 1, 3));
		greenhouseAccess.registerGreenhouseGlass(new ItemStack(Blocks.stained_glass, 1, 4));
		greenhouseAccess.registerGreenhouseGlass(new ItemStack(Blocks.stained_glass, 1, 5));
		greenhouseAccess.registerGreenhouseGlass(new ItemStack(Blocks.stained_glass, 1, 6));
		greenhouseAccess.registerGreenhouseGlass(new ItemStack(Blocks.stained_glass, 1, 7));
		greenhouseAccess.registerGreenhouseGlass(new ItemStack(Blocks.stained_glass, 1, 8));
		greenhouseAccess.registerGreenhouseGlass(new ItemStack(Blocks.stained_glass, 1, 9));
		greenhouseAccess.registerGreenhouseGlass(new ItemStack(Blocks.stained_glass, 1, 10));
		greenhouseAccess.registerGreenhouseGlass(new ItemStack(Blocks.stained_glass, 1, 11));
		greenhouseAccess.registerGreenhouseGlass(new ItemStack(Blocks.stained_glass, 1, 12));
		greenhouseAccess.registerGreenhouseGlass(new ItemStack(Blocks.stained_glass, 1, 13));
		greenhouseAccess.registerGreenhouseGlass(new ItemStack(Blocks.stained_glass, 1, 14));
		greenhouseAccess.registerGreenhouseGlass(new ItemStack(Blocks.stained_glass, 1, 15));
		
		proxy.initializeModels();
	}
	
	@Override
	public IPacketRegistry getPacketRegistry() {
		return new PacketRegistryGreenhouse();
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void handleTextureRemap(TextureStitchEvent.Pre event) {
		BlockGreenhouseType.registerSprites();
	}
	
}

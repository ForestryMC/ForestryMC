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
package forestry.core.climate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import forestry.api.climate.IClimateRegion;
import forestry.api.core.ForestryAPI;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ClimateEventHandler {

	private final Map<World, Integer> serverTicks;

	public ClimateEventHandler() {
		serverTicks = new HashMap<>();
	}

	//Climate test mode
	/*public static Pair<BlockPos, BlockPos> recalculateMinMaxCoords(Collection<BlockPos> positions) {
		BlockPos minimumCoord = new BlockPos(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
		BlockPos maximumCoord = new BlockPos(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

		for (BlockPos position : positions) {
			int minX = minimumCoord.getX();
			int minY = minimumCoord.getY();
			int minZ = minimumCoord.getZ();
			int maxX = maximumCoord.getX();
			int maxY = maximumCoord.getY();
			int maxZ = maximumCoord.getZ();
			if (position.getX() < minimumCoord.getX()) {
				minX = position.getX();
			}
			if (position.getX() > maximumCoord.getX()) {
				maxX = position.getX();
			}
			if (position.getY() < minimumCoord.getY()) {
				minY = position.getY();
			}
			if (position.getY() > maximumCoord.getY()) {
				maxY = position.getY();
			}
			if (position.getZ() < minimumCoord.getZ()) {
				minZ = position.getZ();
			}
			if (position.getZ() > maximumCoord.getZ()) {
				maxZ = position.getZ();
			}
			minimumCoord = new BlockPos(minX, minY, minZ);
			maximumCoord = new BlockPos(maxX, maxY, maxZ);
		}
		return Pair.of(minimumCoord, maximumCoord);
	}
	
	@SubscribeEvent
	public void onWorldRenderLast(RenderWorldLastEvent event){
		try{
	        Tessellator tessellator = Tessellator.getInstance();
	        VertexBuffer buffer = tessellator.getBuffer();
	        float partialTicks = event.getPartialTicks();
	        EntityPlayer entityplayer = Minecraft.getMinecraft().player;
	        double d0 = entityplayer.lastTickPosX + (entityplayer.posX - entityplayer.lastTickPosX) * partialTicks;
	        double d1 = entityplayer.lastTickPosY + (entityplayer.posY - entityplayer.lastTickPosY) * partialTicks;
	        double d2 = entityplayer.lastTickPosZ + (entityplayer.posZ - entityplayer.lastTickPosZ) * partialTicks;
	        GlStateManager.pushMatrix();
	        GlStateManager.enableBlend();
	        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
	        GlStateManager.disableTexture2D();
	        GlStateManager.glLineWidth(5.0F);
			World world = Minecraft.getMinecraft().world;
			List<IClimateRegion> regions = ForestryAPI.climateManager.getRegions().get(Integer.valueOf(world.provider.getDimension()));
			for(IClimateRegion region : regions){
				if(region != null){
					Map<BlockPos, IClimatePosition> positions = region.getPositions();
					Pair<BlockPos, BlockPos> posPair = recalculateMinMaxCoords(positions.keySet());
					int deltaX = posPair.getRight().getX() - posPair.getLeft().getX();
					deltaX+=2;
					int deltaZ = posPair.getRight().getZ() - posPair.getLeft().getZ();
					deltaZ+=3;
					getClass();
					for(Entry<BlockPos, IClimatePosition> positionEntry : positions.entrySet()){
						BlockPos pos = positionEntry.getKey();
						if(pos != null && world.isBlockLoaded(pos)){
							IClimatePosition position = positionEntry.getValue();
							float red = 1.0F;
							float green = 1.0F;
							float blue = 1.0F;
							int offset = 0;
							if(position.getTemperature() >= 2){
								green = 0F;
								blue  = 0F;
							}
							else if(position.getTemperature() > 1.75){
								green = 0.25F;
								blue  = 0.25F;
								offset = 1;
							}
							else if(position.getTemperature() > 1.5){
								green = 0.5F;
								blue  = 0.5F;
								offset = 2;
							}
							else if(position.getTemperature() > 1.25){
								green = 0.75F;
								blue  = 0.75F;
								offset = 3;
							}
							else if(position.getTemperature() > 0.75){
								green = 0.25F;
								red  = 0.25F;
								offset = 4;
							}
							else if(position.getTemperature() > 0.5){
								green = 0.5F;
								red  = 0.5F;
								offset = 5;
							}
							else if(position.getTemperature() > 0.25){
								green = 0.75F;
								red  = 0.75F;
								offset = 6;
							}
							event.getContext().drawSelectionBoundingBox(Block.FULL_BLOCK_AABB.offset(pos.getX()  -d0, pos.getY()  -d1, pos.getZ()  -d2).offset(new BlockPos(deltaX - 1 * deltaX, 0, deltaZ)), red, green, blue, 0.35F);
							event.getContext().renderFilledBox(Block.FULL_BLOCK_AABB.offset(pos.getX()  -d0, pos.getY()  -d1, pos.getZ()  -d2).offset(new BlockPos(deltaX - 1 * deltaX, 0, deltaZ)), red, green, blue, 0.35F);
							event.getContext().drawSelectionBoundingBox(Block.FULL_BLOCK_AABB.offset(pos.getX()  -d0, pos.getY()  -d1, pos.getZ()  -d2).offset(new BlockPos(deltaX + offset * deltaX, 0, deltaZ)), red, green, blue, 0.35F);
							event.getContext().renderFilledBox(Block.FULL_BLOCK_AABB.offset(pos.getX()  -d0, pos.getY()  -d1, pos.getZ()  -d2).offset(new BlockPos(deltaX + offset * deltaX, 0, deltaZ)), red, green, blue, 0.35F);
						}
					}
				}
			}
	        GlStateManager.enableTexture2D();
	        GlStateManager.disableBlend();
	        GlStateManager.popMatrix();
		}catch(Exception e){
			
		}
	}*/

	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent event) {
		World world = event.world;
		if (event.phase == TickEvent.Phase.END) {
			MinecraftServer server = world.getMinecraftServer();
			if (server != null) {
				server.addScheduledTask(() -> {
					if (!serverTicks.containsKey(world)) {
						serverTicks.put(world, 1);
					}
					int ticks = serverTicks.get(world);
					Map<World, List<IClimateRegion>> regions = ForestryAPI.climateManager.getRegions();
					if (regions.containsKey(world)) {
						for (IClimateRegion region : regions.get(world)) {
							region.updateClimate(ticks);
						}
					}
					serverTicks.put(world, ticks + 1);
				});
			}
		}
	}

}

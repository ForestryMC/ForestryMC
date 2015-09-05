package forestry.core.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import forestry.arboriculture.WoodType;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Loader;

public class AutoJsonEditor {

	public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	public static void buildFiles()
	{
		try{
			File arboriculture = new File(Loader.instance().getConfigDir(), "arboriculture");
				
			File stairs = new File(arboriculture, "stair");
			File fences = new File(arboriculture, "fences");
			File logs = new File(arboriculture, "logs");
			File stairItems = new File(arboriculture, "items/stairs");
			File logItems = new File(arboriculture, "items/logs");
			File planks = new File(arboriculture, "planks");
			File planksItems = new File(arboriculture, "items/planks");
			File slabs = new File(arboriculture, "slabs");
			File slabsItems = new File(arboriculture, "items/slabs");
			for(WoodType type : WoodType.values())
			{
				HashMap map = new HashMap<String, String>();
				map.put("bottom", "forestry:blocks/wood/planks." + type.name().toLowerCase());
				map.put("top", "forestry:blocks/wood/planks." + type.name().toLowerCase());
				map.put("side", "forestry:blocks/wood/planks." + type.name().toLowerCase());
				File stair = new File(stairs, type.getName().toLowerCase() + ".json");
				File stair_inner = new File(stairs, type.getName().toLowerCase() + "_inner.json");
				File stair_outer = new File(stairs, type.getName().toLowerCase() + "_outer.json");
                stair.getParentFile().mkdirs();
				stair.createNewFile();
				BufferedWriter writerStairs = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(stair)));
				writerStairs.write(gson.toJson(new AutoJson("block/stairs", map)).replace('[', '{').replace(']', '}'));
				writerStairs.close();
                stair_inner.getParentFile().mkdirs();
				stair_inner.createNewFile();
				BufferedWriter writerStairs_inner  = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(stair_inner)));
				writerStairs_inner.write(gson.toJson(new AutoJson("block/inner_stairs", map)).replace('[', '{').replace(']', '}'));
				writerStairs_inner.close();
				stair_outer.getParentFile().mkdirs();
				stair_outer.createNewFile();
				BufferedWriter writerStairs_outer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(stair_outer)));
				writerStairs_outer.write(gson.toJson(new AutoJson("block/outer_stairs", map)).replace('[', '{').replace(']', '}'));
				writerStairs_outer.close();
				
				HashMap mapPlanks = new HashMap<String, String>();
				mapPlanks.put("all", "forestry:blocks/wood/planks." + type.name().toLowerCase());
				HashMap mapSlabs = new HashMap<String, String>();
				mapSlabs.put("bottom", "forestry:blocks/wood/planks." + type.name().toLowerCase());
				mapSlabs.put("top", "forestry:blocks/wood/planks." + type.name().toLowerCase());
				mapSlabs.put("side", "forestry:blocks/wood/planks." + type.name().toLowerCase());
				File plank = new File(planks, type.getName().toLowerCase() + ".json");
				File slab = new File(slabs, type.getName().toLowerCase() + "_half.json");
				File slabUpper = new File(slabs, type.getName().toLowerCase() + "_upper.json");
                plank.getParentFile().mkdirs();
				plank.createNewFile();
				BufferedWriter writerPlanks = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(plank)));
				writerPlanks.write(gson.toJson(new AutoJson("block/cube_all", mapPlanks)).replace('[', '{').replace(']', '}'));
				writerPlanks.close();
                slab.getParentFile().mkdirs();
				slab.createNewFile();
				BufferedWriter writerSlab  = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(slab)));
				writerSlab.write(gson.toJson(new AutoJson("block/half_slab", mapSlabs)).replace('[', '{').replace(']', '}'));
				writerSlab.close();
                slabUpper.getParentFile().mkdirs();
				slabUpper.createNewFile();
				BufferedWriter writerSlabUpper  = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(slabUpper)));
				writerSlabUpper.write(gson.toJson(new AutoJson("block/upper_slab", mapSlabs)).replace('[', '{').replace(']', '}'));
				writerSlabUpper.close();
				
				HashMap mapLog = new HashMap<String, String>();
				mapLog.put("end", "forestry:blocks/wood/heart." + type.name().toLowerCase());
				mapLog.put("side", "forestry:blocks/wood/bark." + type.name().toLowerCase());
				File log = new File(logs, type.getName().toLowerCase() + ".json");
				File log_side = new File(logs, type.getName().toLowerCase() + "_side.json");
                log.getParentFile().mkdirs();
				log.createNewFile();
				BufferedWriter writerLogLogs = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(log)));
				writerLogLogs.write(gson.toJson(new AutoJson("block/cube_column", mapLog)).replace('[', '{').replace(']', '}'));
				writerLogLogs.close();
				log_side.getParentFile().mkdirs();
				log_side.createNewFile();
				BufferedWriter writerLogsSide = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(log_side)));
				writerLogsSide.write(gson.toJson(new AutoJson("block/column_side", mapLog)).replace('[', '{').replace(']', '}'));
				writerLogsSide.close();
				
				HashMap mapFence = new HashMap<String, String>();
				mapFence.put("texture", "forestry:blocks/wood/planks." + type.name().toLowerCase());
				File fence_inventory = new File(fences, type.getName().toLowerCase() + "_inventory.json");
				File fence_post = new File(fences, type.getName().toLowerCase() + "_post.json");
				File fence_n = new File(fences, type.getName().toLowerCase() + "_n.json");
				File fence_ne = new File(fences, type.getName().toLowerCase() + "_ne.json");
				File fence_ns = new File(fences, type.getName().toLowerCase() + "_ns.json");
				File fence_nse = new File(fences, type.getName().toLowerCase() + "_nse.json");
				File fence_nsew = new File(fences, type.getName().toLowerCase() + "_nsew.json");
                fence_inventory.getParentFile().mkdirs();
				fence_inventory.createNewFile();
				BufferedWriter writerFenceInventory = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fence_inventory)));
				writerFenceInventory.write(gson.toJson(new AutoJson("block/fence_inventory", mapFence)).replace('[', '{').replace(']', '}'));
				writerFenceInventory.close();
                fence_post.getParentFile().mkdirs();
				fence_post.createNewFile();
				BufferedWriter writerFencesPost = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fence_post)));
				writerFencesPost.write(gson.toJson(new AutoJson("block/fence_post", mapFence)).replace('[', '{').replace(']', '}'));
				writerFencesPost.close();
                fence_n.getParentFile().mkdirs();
				fence_n.createNewFile();
				BufferedWriter writerStairsN = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fence_n)));
				writerStairsN.write(gson.toJson(new AutoJson("block/fence_n", mapFence)).replace('[', '{').replace(']', '}'));
				writerStairsN.close();
				fence_ne.getParentFile().mkdirs();
				fence_ne.createNewFile();
				BufferedWriter writerStairsNE = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fence_ne)));
				writerStairsNE.write(gson.toJson(new AutoJson("block/fence_ne", mapFence)).replace('[', '{').replace(']', '}'));
				writerStairsNE.close();
				fence_ns.getParentFile().mkdirs();
				fence_ns.createNewFile();
				BufferedWriter writerStairsNS = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fence_ns)));
				writerStairsNS.write(gson.toJson(new AutoJson("block/fence_ns", mapFence)).replace('[', '{').replace(']', '}'));
				writerStairsNS.close();
				fence_nse.getParentFile().mkdirs();
				fence_nse.createNewFile();
				BufferedWriter writerStairsNSE = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fence_nse)));
				writerStairsNSE.write(gson.toJson(new AutoJson("block/fence_nse", mapFence)).replace('[', '{').replace(']', '}'));
				writerStairsNSE.close();
				fence_nsew.getParentFile().mkdirs();
				fence_nsew.createNewFile();
				BufferedWriter writerStairsNSEW = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fence_nsew)));
				writerStairsNSEW.write(gson.toJson(new AutoJson("block/fence_nsew", mapFence)).replace('[', '{').replace(']', '}'));
				writerStairsNSEW.close();
				
				if(type == WoodType.LARCH)
				{
					continue;
				}
				
				File itemOld = new File(stairItems, "larch.json");
				File item = new File(stairItems, type.getName().toLowerCase() + ".json");
				item.getParentFile().mkdirs();
				item.createNewFile();
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(itemOld)));
				String line;
				StringBuilder builder = new StringBuilder();
				while((line = reader.readLine()) != null)
					builder.append(line.replace("larch", type.getName().toLowerCase()) + "\n");
				reader.close();
				String json = builder.toString();
				BufferedWriter writerStairItems = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(item)));
				writerStairItems.write(json);
				writerStairItems.close();
				
				File fenceItems = new File(arboriculture, "items/fences");
				File itemOldFence = new File(fenceItems, "larch.json");
				File itemFence = new File(fenceItems, type.getName().toLowerCase() + ".json");
				itemFence.getParentFile().mkdirs();
				itemFence.createNewFile();
				BufferedReader readerFence = new BufferedReader(new InputStreamReader(new FileInputStream(itemOldFence)));
				String lineFence;
				StringBuilder builderFence = new StringBuilder();
				while((lineFence = readerFence.readLine()) != null)
					builderFence.append(lineFence.replace("larch", type.getName().toLowerCase()) + "\n");
				readerFence.close();
				String jsonFence = builderFence.toString();
				BufferedWriter writerFenceItems = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(itemFence)));
				writerFenceItems.write(jsonFence);
				writerFenceItems.close();
				
				File itemOldLog = new File(logItems, "larch.json");
				File itemLog = new File(logItems, type.getName().toLowerCase() + ".json");
				itemLog.getParentFile().mkdirs();
				itemLog.createNewFile();
				BufferedReader readerLog = new BufferedReader(new InputStreamReader(new FileInputStream(itemOldLog)));
				String lineLog;
				StringBuilder builderLog = new StringBuilder();
				while((lineLog = readerLog.readLine()) != null)
					builderLog.append(lineLog.replace("larch", type.getName().toLowerCase()) + "\n");
				readerLog.close();
				String jsonLog = builderLog.toString();
				BufferedWriter writerLogItems = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(itemLog)));
				writerLogItems.write(jsonLog);
				writerLogItems.close();
				
				File itemOldPlank = new File(planksItems, "larch.json");
				File itemPlank = new File(planksItems, type.getName().toLowerCase() + ".json");
				itemPlank.getParentFile().mkdirs();
				itemPlank.createNewFile();
				BufferedReader readerPlank = new BufferedReader(new InputStreamReader(new FileInputStream(itemOldPlank)));
				String linePlank;
				StringBuilder builderPlank = new StringBuilder();
				while((linePlank = readerPlank.readLine()) != null)
					builderPlank.append(linePlank.replace("larch", type.getName().toLowerCase()) + "\n");
				readerPlank.close();
				String jsonPlank = builderPlank.toString();
				BufferedWriter writerLogPlank = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(itemPlank)));
				writerLogPlank.write(jsonPlank);
				writerLogPlank.close();
				
				File itemOldSlab = new File(slabsItems, "larch.json");
				File itemSlab = new File(slabsItems, type.getName().toLowerCase() + ".json");
				itemSlab.getParentFile().mkdirs();
				itemSlab.createNewFile();
				BufferedReader readerSlab = new BufferedReader(new InputStreamReader(new FileInputStream(itemOldSlab)));
				String lineSlab;
				StringBuilder builderSlab = new StringBuilder();
				while((lineSlab = readerSlab.readLine()) != null)
					builderSlab.append(lineSlab.replace("larch", type.getName().toLowerCase()) + "\n");
				readerSlab.close();
				String jsonSlab = builderSlab.toString();
				BufferedWriter writerLogSlab = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(itemSlab)));
				writerLogSlab.write(jsonSlab);
				writerLogSlab.close();
				
			}
		}
		catch(Exception e){
			FMLClientHandler.instance().haltGame("FML will not run in demo mode", e);
		}
	}
	
}

package forestry.arboriculture.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraft.world.gen.feature.WorldGenerator;

public class CommandTreesSpawnTree extends CommandTreesSpawn {

	public CommandTreesSpawnTree() {
		super("spawnTree");
	}

	@Override
	protected void processSubCommand(ICommandSender sender, String treeName, EntityPlayer player) {
		Vec3 look = player.getLookVec();

		int x = (int)Math.round(player.posX + (3 * look.xCoord));
		int y = (int)Math.round(player.posY);
		int z = (int)Math.round(player.posZ + (3 * look.zCoord));

		WorldGenerator gen = getWorldGen(treeName, player, x, y, z);
		if (gen == null) {
			printHelp(sender);
			return;
		}

		generateTree(gen, player, x, y, z);
	}

}

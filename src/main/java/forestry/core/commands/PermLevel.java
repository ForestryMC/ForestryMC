package forestry.core.commands;

import java.util.function.Predicate;

import net.minecraft.command.CommandSource;

public enum PermLevel implements Predicate<CommandSource> {

    EVERYONE(0), ADMIN(2);
    public final int permLevel;

    PermLevel(int permLevel) {
        this.permLevel = permLevel;
    }

    @Override
    public boolean test(CommandSource commandSource) {
        return commandSource.hasPermissionLevel(permLevel);
    }
}

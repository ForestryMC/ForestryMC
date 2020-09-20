package forestry.core.commands;

import net.minecraft.command.CommandSource;

import java.util.function.Predicate;

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
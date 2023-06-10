package org.metamechanists.death_lasers.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Private;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import io.github.bakedlibs.dough.common.ChatColors;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.metamechanists.death_lasers.lasers.storage.BeamStorage;


@SuppressWarnings("unused")
@CommandAlias("deathlasers")
public class DeathLaserCommand extends BaseCommand {

    @HelpCommand
    @Syntax("")
    @Private
    public void helpCommand(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("getbeamstorage")
    @Description("[Debug] Shows the BeamGroups in storage")
    @CommandPermission("death_lasers.command.getbeamstorage")
    public void getbeamstorage(Player player) {
        final StringBuilder builder = new StringBuilder();

        builder.append("&7Deprecated BeamGroups: &e").append(BeamStorage.getNumberOfDeprecatedBeamGroups()).append("\n");
        builder.append("&7Active BeamGroups: &e").append(BeamStorage.getNumberOfActiveBeamGroups()).append("\n");
        builder.append("&7Active BeamGroup Locations:&f").append("\n");

        BeamStorage.getBeamGroups()
                .forEach((location, beamGroup) -> builder
                        .append(location.toString())
                        .append(" &b-> &f")
                        .append(beamGroup.toString())
                        .append("\n"));

        player.sendMessage(ChatColors.color(builder.toString()));
    }
}

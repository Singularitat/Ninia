package Ninia.commands;

import Ninia.utils.Command;
import Ninia.utils.CommandEvent;
import java.time.format.DateTimeFormatter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;


public class ServerCommand extends Command
{
    public ServerCommand()
    {
        this.name = "server";
        this.help = "Shows some information about the current server";
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        Guild guild = event.getGuild();
        Member owner = guild.getOwner();
        long onlineCount = guild.getMembers().stream().filter(u -> u.getOnlineStatus() != OnlineStatus.OFFLINE).count();
        long botCount = guild.getMembers().stream().filter(m -> m.getUser().isBot()).count();
        EmbedBuilder embed = new EmbedBuilder();

        String str = "ID: **" + guild.getId() + "**\n"
                + "Owner: " + (owner == null ? "Unknown" : "**" + owner.getUser().getName() + "**#" + owner.getUser().getDiscriminator()) + "\n"
                + "Location: " + (guild.getRegion().getEmoji().isEmpty() ? "\u2754" : guild.getRegion().getEmoji()) + " **" + guild.getRegion().getName() + "**\n"
                + "Creation: **" + guild.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME) + "**\n"
                + "Users: **" + guild.getMemberCache().size() + "** (" + onlineCount + " online, " + botCount + " bots)\n"
                + "Channels: **" + guild.getTextChannelCache().size() + "** Text, **" + guild.getVoiceChannelCache().size() + "** Voice, **" + guild.getCategoryCache().size() + "** Categories\n";
        if(guild.getIconUrl()!=null)
            embed.setThumbnail(guild.getIconUrl());
        embed.setColor(owner == null ? null : owner.getColor());
        embed.setDescription(str);
        event.reply(embed.build());
    }
}

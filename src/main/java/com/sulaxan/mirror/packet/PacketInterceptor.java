package com.sulaxan.mirror.packet;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import org.bukkit.entity.Player;

import java.net.InetSocketAddress;
import java.util.Optional;

@Data
public class PacketInterceptor {

    private final PacketHandler handler;

    public void startIntercepting(Player player) {
        var conOpt = getConnection(player);
        if (conOpt.isEmpty()) {
            System.out.println("Could not find associated connection for " + player.getName());
            return;
        }


        var con = conOpt.get();
        con.channel.pipeline().addBefore("packet_handler", "mirror_interceptor", new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                super.channelRead(ctx, msg);
                handler.handle(player, con, msg);
            }
        });
    }

    public void stopIntercepting(Player player) {
        var conOpt = getConnection(player);
        if (conOpt.isEmpty()) {
            System.out.println("Could not find associated connection for " + player.getName());
            return;
        }

        conOpt.get().channel.pipeline().remove("mirror_interceptor");
    }

    private Optional<Connection> getConnection(Player player) {
        return MinecraftServer.getServer().getConnection().getConnections().stream().filter(c -> {
            if (c.address instanceof InetSocketAddress add) {
                return add.equals((player.getAddress()));
            }

            return false;
        }).findFirst();
    }
}

package com.sulaxan.mirror;

import com.sulaxan.mirror.packet.PacketHandler;
import com.sulaxan.mirror.packet.PacketInterceptor;
import net.minecraft.server.MinecraftServer;
import org.bukkit.plugin.java.JavaPlugin;

public final class Mirror extends JavaPlugin {

    private MirrorManager mirrorManager;

    @Override
    public void onEnable() {
        mirrorManager = new MirrorManager(this);
        getServer().getPluginCommand("mirror").setExecutor(new MirrorCommand(mirrorManager));
    }

    @Override
    public void onDisable() {
        // remove packet interceptor from everyone
        MinecraftServer.getServer().getConnection().getConnections().forEach(c -> c.channel.pipeline().remove("mirror_interceptor"));
    }
}

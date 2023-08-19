package com.sulaxan.mirror;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.sulaxan.mirror.packet.PacketHandler;
import com.sulaxan.mirror.packet.PacketInterceptor;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.profile.CraftPlayerProfile;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Represents the mirror area.
 */
@Data
public class MirrorBox {

    @NonNull
    private final Location bboxA;
    @NonNull
    private final Location bboxB;

    private PacketHandler packetHandler;
    private PacketInterceptor packetInterceptor;

    @Getter
    private Map<String, ServerPlayer> npcs = Maps.newHashMap();
    private int currentTick = 0;

    public MirrorBox(@NonNull Location bboxA, @NonNull Location bboxB) {
        this.bboxA = bboxA;
        this.bboxB = bboxB;

        this.packetHandler = new PacketHandler(this);
        this.packetInterceptor = new PacketInterceptor(packetHandler);
    }

    public void init() {
    }

    public World getWorld() {
        return bboxA.getWorld();
    }

    public Location getCenter() {
        return new Location(
                bboxA.getWorld(),
                (double) (bboxA.getBlockX() + bboxB.getBlockX()) / 2,
                Math.min(bboxA.getBlockY(), bboxB.getBlockY()), // keep center at the bottom of box
                (double) (bboxA.getBlockZ() + bboxB.getBlockZ()) / 2
        );
    }

    public boolean isInside(Location loc) {
        var minX = Math.min(bboxA.getX(), bboxB.getX());
        var maxX = Math.max(bboxA.getX(), bboxB.getX());
        var minY = Math.min(bboxA.getY(), bboxB.getY());
        var maxY = Math.max(bboxA.getY(), bboxB.getY());
        var minZ = Math.min(bboxA.getZ(), bboxB.getZ());
        var maxZ = Math.max(bboxA.getZ(), bboxB.getZ());

        return loc.getX() >= minX && loc.getX() <= maxX &&
                loc.getY() >= minY && loc.getY() <= maxY &&
                loc.getZ() >= minZ && loc.getZ() <= maxZ;
    }

    // mirrors the location, taking into account the center and where the mirror line is
    public Location mirror(Location loc) {
        // mirror is along the y-axis; we want to do a y rotation (around the center point, hence the
        // -center.get...)
        Location center = getCenter();
        double locX = loc.getX() - center.getX();
        double locZ = loc.getZ() - center.getZ();

        // pi = 180 degrees
        double x = locX * Math.cos(Math.PI) + locZ * Math.sin(Math.PI);
        double y = loc.getY();
        // keep z as is
        double z = loc.getZ(); // -locX * Math.sin(Math.PI) + locZ * Math.cos(Math.PI);
        float yaw = -loc.getYaw();
        float pitch = loc.getPitch();

        x += center.getX();

        return new Location(loc.getWorld(), x, y, z, yaw, pitch);
    }

    public void update() {
        currentTick++;
        // nothing... yet
    }

    public void createNpc(Player player) {
        if (npcs.containsKey(player.getUniqueId().toString())) {
            return;
        }

        CraftPlayer craftPlayer = (CraftPlayer) player;

        var profile = new CraftPlayerProfile(UUID.randomUUID(), StringUtils.reverse(player.getName()));
        profile.setTextures(craftPlayer.getPlayerProfile().getTextures());
        var gameProfile = profile.buildGameProfile();
        gameProfile.getProperties().putAll(craftPlayer.getProfile().getProperties());

        var npc = new ServerPlayer(
                MinecraftServer.getServer(),
                MinecraftServer.getServer().overworld(),
                gameProfile
        );
        npc.setPos(player.getLocation().getX(), player.getLocation().getY() + 3, player.getLocation().getZ());

        ClientboundPlayerInfoUpdatePacket packet = new ClientboundPlayerInfoUpdatePacket(
                ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER,
                npc
        );
        ClientboundAddPlayerPacket packet2 = new ClientboundAddPlayerPacket(npc);

        craftPlayer.getHandle().connection.send(packet);
        craftPlayer.getHandle().connection.send(packet2);

        npcs.put(player.getUniqueId().toString(), npc);
    }

    public void destroyNpc(Player player) {
        var npc = npcs.remove(player.getUniqueId().toString());
        if (npc == null) {
            return;
        }

        CraftPlayer craftPlayer = (CraftPlayer) player;

        ClientboundPlayerInfoRemovePacket packet = new ClientboundPlayerInfoRemovePacket(
                Collections.singletonList(npc.getUUID())
        );
        ClientboundRemoveEntitiesPacket packet2 = new ClientboundRemoveEntitiesPacket(npc.getId());

        craftPlayer.getHandle().connection.send(packet);
        craftPlayer.getHandle().connection.send(packet2);
    }

    public void constructBox() {
        World world = bboxA.getWorld();
        var minY = Math.min(bboxA.getBlockY(), bboxB.getBlockY());
        var maxY = Math.max(bboxA.getBlockY(), bboxB.getBlockY());

        constructWall(
                world,
                Material.STONE,
                Math.min(bboxA.getBlockX(), bboxB.getBlockX()),
                minY,
                bboxA.getBlockZ(),
                Math.max(bboxA.getBlockX(), bboxB.getBlockX()),
                maxY,
                bboxA.getBlockZ()
        );
        constructWall(
                world,
                Material.STONE,
                Math.min(bboxA.getBlockX(), bboxB.getBlockX()),
                minY,
                bboxB.getBlockZ(),
                Math.max(bboxA.getBlockX(), bboxB.getBlockX()),
                maxY,
                bboxB.getBlockZ()
        );
        constructWall(
                world,
                Material.STONE,
                bboxA.getBlockX(),
                minY,
                Math.min(bboxA.getBlockZ(), bboxB.getBlockZ()),
                bboxA.getBlockX(),
                maxY,
                Math.max(bboxA.getBlockZ(), bboxB.getBlockZ())
        );
        constructWall(
                world,
                Material.STONE,
                bboxB.getBlockX(),
                minY,
                Math.min(bboxA.getBlockZ(), bboxB.getBlockZ()),
                bboxB.getBlockX(),
                maxY,
                Math.max(bboxA.getBlockZ(), bboxB.getBlockZ())
        );

        // mirror
        var halfX = (bboxA.getBlockX() + bboxB.getBlockX()) / 2;

        // inset the glass a bit (the +/- 1 for z) so it doesn't replace the bounding box wall
        constructWall(
                world,
                Material.GLASS,
                halfX,
                minY,
                Math.min(bboxA.getBlockZ(), bboxB.getBlockZ()) + 1,
                halfX,
                maxY,
                Math.max(bboxA.getBlockZ(), bboxB.getBlockZ()) - 1
        );
    }

    private void constructWall(World world, Material block, int x1, int y1, int z1, int x2, int y2, int z2) {
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    Location loc = new Location(world, x, y, z);
                    loc.getBlock().setType(block);
                }
            }
        }
    }
}

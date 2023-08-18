package com.sulaxan.mirror.packet;

import com.mojang.datafixers.util.Pair;
import com.sulaxan.mirror.MirrorBox;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.util.List;

@RequiredArgsConstructor
public class PacketHandler {

    private final MirrorBox box;

    public void handle(Player player, Connection con, Object msg) {
        var npc = box.getNpcs().getOrDefault(player.getUniqueId().toString(), null);
        if (npc == null) {
            return;
        }

        if (msg instanceof ServerboundSwingPacket p) {
            ClientboundAnimatePacket packet = new ClientboundAnimatePacket(
                    npc,
                    // we want to swap the hands (mirror effect)
                    p.getHand() == InteractionHand.MAIN_HAND ?
                            ClientboundAnimatePacket.SWING_OFF_HAND : ClientboundAnimatePacket.SWING_MAIN_HAND
            );
            con.send(packet);
        }
        if (msg instanceof ServerboundMovePlayerPacket p) {
            Location mirrored = box.mirror(new Location(box.getWorld(), p.x, p.y, p.z, p.yRot, p.xRot));
            if (p.hasPos) {
                npc.setPos(mirrored.getX(), mirrored.getY(), mirrored.getZ());
            }
            if (p.hasRot) {
                npc.setXRot(mirrored.getPitch());
                npc.setYRot(mirrored.getYaw());

                // this packet needs to be sent to update the head position
                ClientboundRotateHeadPacket packet = new ClientboundRotateHeadPacket(npc, (byte) (npc.getYRot() * 256.0F / 360.0F));

                con.send(packet);
            }

            ClientboundTeleportEntityPacket packet = new ClientboundTeleportEntityPacket(npc);
            con.send(packet);
        }
        if (msg instanceof ServerboundPlayerInputPacket p) {
            if (p.isShiftKeyDown()) {
            }
        }
        if (msg instanceof ServerboundSetCarriedItemPacket p) {
            var packet = new ClientboundSetEquipmentPacket(
                    npc.getId(),
                    List.of(Pair.of(EquipmentSlot.MAINHAND, CraftItemStack.asNMSCopy(player.getInventory().getItem(p.getSlot()))))
            );

            con.send(packet);
        }
        if (msg instanceof ServerboundChatPacket p) {
            var message = StringUtils.reverse(p.message()) + " <" + npc.getName().getString() + ">";
            Bukkit.broadcastMessage(message);
        }
    }
}

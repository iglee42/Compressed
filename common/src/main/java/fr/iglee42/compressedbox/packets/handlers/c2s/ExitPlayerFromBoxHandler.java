package fr.iglee42.compressedbox.packets.handlers.c2s;

import com.mojang.datafixers.util.Pair;
import dev.architectury.networking.NetworkManager;
import fr.iglee42.compressedbox.packets.payloads.c2s.ExitPlayerFromBoxPayload;
import fr.iglee42.compressedbox.utils.BoxesSaveData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.Set;


public class ExitPlayerFromBoxHandler {

    private static final ExitPlayerFromBoxHandler INSTANCE = new ExitPlayerFromBoxHandler();

    public static ExitPlayerFromBoxHandler instance(){
        return INSTANCE;
    }

    public void handle(final ExitPlayerFromBoxPayload payload, final NetworkManager.PacketContext context) {
        context.queue(()->{
            ServerPlayer player = (ServerPlayer) context.getPlayer();
            BoxesSaveData data = BoxesSaveData.get(player.level());
            data.exitPlayerFromBox(player);

        });
    }


}

package fr.iglee42.compressedbox.registries;

import dev.architectury.networking.NetworkChannel;
import fr.iglee42.compressedbox.CompressedBox;
import fr.iglee42.compressedbox.packets.c2s.ExitPlayerFromBoxPacket;
import fr.iglee42.compressedbox.packets.c2s.SetBoxNamePacket;
import fr.iglee42.compressedbox.packets.c2s.SetPlayerBoxSpawnPacket;
import fr.iglee42.compressedbox.packets.s2c.*;
import net.minecraft.resources.ResourceLocation;

public class CNetworking {

    public static final NetworkChannel CHANNEL = NetworkChannel.create(new ResourceLocation(CompressedBox.MODID, "messages"));
    public static void register(){
        CHANNEL.register(ExitPlayerFromBoxPacket.class, ExitPlayerFromBoxPacket::encode,ExitPlayerFromBoxPacket::new,ExitPlayerFromBoxPacket::handle);
        CHANNEL.register(SetBoxNamePacket.class, SetBoxNamePacket::encode,SetBoxNamePacket::new,SetBoxNamePacket::handle);
        CHANNEL.register(SetPlayerBoxSpawnPacket.class, SetPlayerBoxSpawnPacket::encode, SetPlayerBoxSpawnPacket::new,SetPlayerBoxSpawnPacket::handle);
        CHANNEL.register(OpenClientConfigScreenPacket.class, OpenClientConfigScreenPacket::encode, OpenClientConfigScreenPacket::new, OpenClientConfigScreenPacket::handle);
        CHANNEL.register(OpenTutorialScreenPacket.class, OpenTutorialScreenPacket::encode, OpenTutorialScreenPacket::new, OpenTutorialScreenPacket::handle);
        CHANNEL.register(SyncPlayerCurrentBoxPacket.class, SyncPlayerCurrentBoxPacket::encode, SyncPlayerCurrentBoxPacket::new, SyncPlayerCurrentBoxPacket::handle);
        CHANNEL.register(ClearPlayerCurrentBoxPacket.class, ClearPlayerCurrentBoxPacket::encode, ClearPlayerCurrentBoxPacket::new, ClearPlayerCurrentBoxPacket::handle);
        CHANNEL.register(CopyToClipboardPacket.class, CopyToClipboardPacket::encode, CopyToClipboardPacket::new, CopyToClipboardPacket::handle);
    }
}

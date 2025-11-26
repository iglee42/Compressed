package fr.iglee42.compressedbox.neoforge;


import fr.iglee42.compressedbox.CompressedBox;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = CompressedBox.MODID)
public class CNetworkingNeoForge {
    @SubscribeEvent
    public static void registerNetworking(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(CompressedBox.MODID);

        //registrar.playToServer(ExitPlayerFromBoxPayload.TYPE, ExitPlayerFromBoxPayload.STREAM_CODEC, ExitPlayerFromBoxHandler.instance()::handle);

    }

}
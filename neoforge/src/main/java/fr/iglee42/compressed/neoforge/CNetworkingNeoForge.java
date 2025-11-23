package fr.iglee42.compressed.neoforge;


import fr.iglee42.compressed.Compressed;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Compressed.MODID)
public class CNetworkingNeoForge {
    @SubscribeEvent
    public static void registerNetworking(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Compressed.MODID);

        //registrar.playToServer(ExitPlayerFromBoxPayload.TYPE, ExitPlayerFromBoxPayload.STREAM_CODEC, ExitPlayerFromBoxHandler.instance()::handle);

    }

}
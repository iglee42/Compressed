package fr.iglee42.compressed.commands;

import com.mojang.brigadier.CommandDispatcher;
import dev.architectury.networking.NetworkManager;
import fr.iglee42.compressed.Compressed;
import fr.iglee42.compressed.packets.payloads.s2c.OpenClientConfigScreenPayload;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CCommand{

    public static void create(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(Compressed.MODID)
                .then(Commands.literal("config").executes(ctx->{
                    if (ctx.getSource().isPlayer()){
                        NetworkManager.sendToPlayer(ctx.getSource().getPlayerOrException(), OpenClientConfigScreenPayload.INSTANCE);
                    }
                    return 1;
                }))
        );
    }

}

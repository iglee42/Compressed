package fr.iglee42.compressedbox.blockentities;

import com.mojang.datafixers.util.Pair;
import dev.architectury.fluid.FluidStack;
import fr.iglee42.compressedbox.registries.CBlockEntities;
import fr.iglee42.compressedbox.registries.CDataComponents;
import fr.iglee42.compressedbox.utils.Box;
import fr.iglee42.compressedbox.utils.BoxesSaveData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class CompressedBlockEntity extends BlockEntity {

    @Setter
    @Getter
    private @Nullable UUID boxID = null;

    @Setter(AccessLevel.PRIVATE)
    private @Nullable Box clientBox = null;

    @Setter(AccessLevel.PRIVATE)
    private @Nullable StructureTemplate insideStructure = null;

    @Setter(AccessLevel.PRIVATE)
    @Getter
    private List<ItemStack> clientItems = new ArrayList<>();
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private List<FluidStack> clientFluids = new ArrayList<>();

    public CompressedBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(CBlockEntities.COMPRESSED.get(), blockPos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (boxID != null) {
            tag.putUUID("boxID",boxID);
        }


    }



    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("boxID")) setBoxID(tag.getUUID("boxID"));

        if (tag.contains("box")) setClientBox(Box.CODEC.decode(RegistryOps.create(NbtOps.INSTANCE,registries),tag.get("box")).mapOrElse(Pair::getFirst,e->null));
        if (tag.contains("structureInside")){
            registries.lookup(Registries.BLOCK).ifPresent(h->{
                StructureTemplate template = new StructureTemplate();
                template.load(h,tag.getCompound("structureInside"));
                setInsideStructure(template);
            });
        }
        if (tag.contains("items", Tag.TAG_LIST)){
            setClientItems(ItemStack.CODEC.listOf().decode(RegistryOps.create(NbtOps.INSTANCE,registries),tag.get("items")).mapOrElse(Pair::getFirst,e->new ArrayList<>()));
        }
        if (tag.contains("fluids", Tag.TAG_LIST)){
            setClientFluids(FluidStack.CODEC.listOf().decode(RegistryOps.create(NbtOps.INSTANCE,registries),tag.get("fluids")).mapOrElse(Pair::getFirst,e->new ArrayList<>()));
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput arg) {
        super.applyImplicitComponents(arg);
        setBoxID(arg.getOrDefault(CDataComponents.BOX_ID.get(),null));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        if (boxID != null) builder.set(CDataComponents.BOX_ID.get(),boxID);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = saveWithoutMetadata(provider);
        if (level != null && !level.isClientSide && getBox() != null){
            tag.put("box",Box.CODEC.encodeStart(RegistryOps.create(NbtOps.INSTANCE,provider),getBox()).mapOrElse(Function.identity(),e->new CompoundTag()));

            StructureTemplate template = getBox().createServerStructureTemplate((ServerLevel) level);
            if (template != null)
                tag.put("structureInside",template.save(new CompoundTag()));

            tag.put("items",ItemStack.CODEC.listOf().encodeStart(provider.createSerializationContext(NbtOps.INSTANCE),getBox().getItems(level).getItems()).mapOrElse(Function.identity(),e->new CompoundTag()));
            tag.put("fluids",FluidStack.CODEC.listOf().encodeStart(provider.createSerializationContext(NbtOps.INSTANCE),getBox().getFluids(level).getFluids()).mapOrElse(Function.identity(),e->new CompoundTag()));
        }
        return tag;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CompressedBlockEntity be){
        if (level != null) level.sendBlockUpdated(pos,state,state, Block.UPDATE_CLIENTS);
    }

    public @Nullable Box getBox(){
        if (level == null) return null;
        if (boxID == null) return null;

        if (level.isClientSide){
            if (clientBox == null){
                return null;
            }
            return clientBox;
        }
        BoxesSaveData data = BoxesSaveData.get(level);
        return data.getBox(boxID);
    }

    public @Nullable StructureTemplate getBlockInside(){
        if (level == null) return null;
        if (!level.isClientSide) return null;
        if (insideStructure == null) {
            return null;
        }
        return insideStructure;
    }
}

package fr.iglee42.compressedbox.blockentities;

import com.mojang.datafixers.util.Pair;
import dev.architectury.fluid.FluidStack;
import fr.iglee42.compressedbox.registries.CBlockEntities;
import fr.iglee42.compressedbox.utils.Box;
import fr.iglee42.compressedbox.utils.BoxesSaveData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
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
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (boxID != null) {
            tag.putUUID("boxID",boxID);
        }


    }


    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("boxID")) setBoxID(tag.getUUID("boxID"));

        if (tag.contains("box") && level!= null && level.isClientSide) {
            try {
                setClientBox(Box.load(tag.getCompound("box")));
            } catch (Exception e){
                setClientBox(null);
            }
        }
        if (tag.contains("structureInside") && level != null && level.isClientSide){
            level.registryAccess().lookup(Registries.BLOCK).ifPresent(h->{
                StructureTemplate template = new StructureTemplate();
                template.load(h,tag.getCompound("structureInside"));
                setInsideStructure(template);
            });
        }
        if (tag.contains("items", Tag.TAG_LIST)){

            ListTag items = tag.getList("items", Tag.TAG_COMPOUND);
            List<ItemStack> clientItems = new ArrayList<>();
            items.stream().map(CompoundTag.class::cast).forEach(it->{
                clientItems.add(ItemStack.of(it));
            });
            setClientItems(clientItems);
        }
        if (tag.contains("fluids", Tag.TAG_LIST)){
            ListTag fluids = tag.getList("fluids", Tag.TAG_COMPOUND);
            List<FluidStack> clientFluids = new ArrayList<>();
            fluids.stream().map(CompoundTag.class::cast).forEach(it->{
                clientFluids.add(FluidStack.read(it));
            });
            setClientFluids(clientFluids);
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = saveWithoutMetadata();
        if (level != null && !level.isClientSide && getBox() != null){
            tag.put("box",getBox().save());

            StructureTemplate template = getBox().createServerStructureTemplate((ServerLevel) level);
            if (template != null)
                tag.put("structureInside",template.save(new CompoundTag()));

            ListTag items = new ListTag();
            getBox().getItems(level).getItems().forEach(it->{
                items.add(it.save(new CompoundTag()));
            });
            tag.put("items",items);
            ListTag fluids = new ListTag();
            getBox().getFluids(level).getFluids().forEach(it->{
                fluids.add(it.write(new CompoundTag()));
            });
            tag.put("fluids",fluids);
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

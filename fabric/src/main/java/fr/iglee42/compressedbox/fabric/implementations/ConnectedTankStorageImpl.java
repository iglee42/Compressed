package fr.iglee42.compressedbox.fabric.implementations;

import dev.architectury.fluid.FluidStack;
import fr.iglee42.compressedbox.containers.fluids.ConnectedTankHandler;
import fr.iglee42.compressedbox.containers.fluids.SimpleFluidContainer;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Fabric Fluid wrapper for ConnectedTankHandler
 */
public class ConnectedTankStorageImpl
        extends CombinedStorage<FluidVariant, SingleSlotStorage<FluidVariant>>{

    private final ConnectedTankHandler handler;
    private final List<TankSlotWrapper> backingList;
    private final MarkDirtyParticipant markDirtyParticipant = new MarkDirtyParticipant();

    public ConnectedTankStorageImpl(ConnectedTankHandler handler) {
        super(Collections.emptyList());
        this.handler = handler;
        this.backingList = new ArrayList<>();
        resizeSlotList();
    }

    /* ------------------------------------------------------------ */
    /* Slot management                                              */
    /* ------------------------------------------------------------ */

    private void resizeSlotList() {
        int size = handler.getContainerSize();

        while (backingList.size() < size) {
            backingList.add(new TankSlotWrapper(this, backingList.size()));
        }

        parts = Collections.unmodifiableList(backingList.subList(0, size));
    }


    /* ------------------------------------------------------------ */
    /* Dirty handling                                               */
    /* ------------------------------------------------------------ */

    class MarkDirtyParticipant extends SnapshotParticipant<Boolean> {

        @Override
        protected Boolean createSnapshot() {
            return Boolean.TRUE;
        }

        @Override
        protected void readSnapshot(Boolean snapshot) {}

        @Override
        protected void onFinalCommit() {
            handler.setChanged();
        }
    }

    /* ------------------------------------------------------------ */
    /* Slot Wrapper                                                 */
    /* ------------------------------------------------------------ */

    static class TankSlotWrapper extends SnapshotParticipant<FluidVariant>
            implements SingleSlotStorage<FluidVariant> {

        private final ConnectedTankStorageImpl parent;
        private final int slot;

        TankSlotWrapper(ConnectedTankStorageImpl parent, int slot) {
            this.parent = parent;
            this.slot = slot;
        }

        @Override
        public long getCapacity() {
            FluidVariant variant = getResource();
            if (variant.isBlank())
                return 0;

            return SimpleFluidContainer.MAX_FLUID;
        }

        @Override
        public FluidVariant getResource() {
            var stack = parent.handler.getFluid(slot);
            if (stack.isEmpty())
                return FluidVariant.blank();

            return FluidVariant.of(stack.getFluid(), stack.getTag());
        }

        @Override
        public long getAmount() {
            return parent.handler.getFluid(slot).getAmount();
        }

        @Override
        public boolean isResourceBlank() {
            return parent.handler.getFluid(slot).isEmpty();
        }

        /* ---------------- Fill ---------------- */

        @Override
        public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {

            if (resource.isBlank() || maxAmount <= 0)
                return 0;

            var existing = parent.handler.getFluid(slot);

            // Different fluid
            if (!existing.isEmpty() &&
                !FluidVariant.of(existing.getFluid(), existing.getTag()).equals(resource))
                return 0;

            long inserted = Math.min(maxAmount, SimpleFluidContainer.MAX_FLUID - existing.getAmount());

            if (inserted <= 0)
                return 0;

            updateSnapshots(transaction);

            if (existing.isEmpty()) {
                parent.handler.setFluid(slot,
                        FluidStack.create(
                                resource.getFluid(),
                                inserted
                        )
                );
            } else {
                existing.grow((int) inserted);
                parent.handler.setFluid(slot, existing);
            }

            parent.markDirtyParticipant.updateSnapshots(transaction);
            return inserted;
        }

        /* ---------------- Drain ---------------- */

        @Override
        public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {

            var existing = parent.handler.getFluid(slot);
            if (existing.isEmpty())
                return 0;

            if (!FluidVariant.of(existing.getFluid(), existing.getTag()).equals(resource))
                return 0;

            long extracted = Math.min(maxAmount, existing.getAmount());

            updateSnapshots(transaction);
            parent.markDirtyParticipant.updateSnapshots(transaction);

            parent.handler.removeFluid(slot, (int) extracted);
            return extracted;
        }

        /* ---------------- Snapshot ---------------- */

        @Override
        protected FluidVariant createSnapshot() {
            return getResource();
        }

        @Override
        protected void readSnapshot(FluidVariant snapshot) {
            if (snapshot.isBlank()) {
                parent.handler.setFluid(slot, dev.architectury.fluid.FluidStack.empty());
            } else {
                parent.handler.setFluid(slot,
                        FluidStack.create(
                                snapshot.getFluid(),
                                parent.handler.getFluid(slot).getAmount()
                        )
                );
            }
        }

    }
}

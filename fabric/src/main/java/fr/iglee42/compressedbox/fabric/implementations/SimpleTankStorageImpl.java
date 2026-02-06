package fr.iglee42.compressedbox.fabric.implementations;

import dev.architectury.fluid.FluidStack;
import fr.iglee42.compressedbox.containers.fluids.ConnectedTankHandler;
import fr.iglee42.compressedbox.containers.fluids.SimpleFluidContainer;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Fabric Fluid wrapper for ConnectedTankHandler
 */
public class SimpleTankStorageImpl
        extends SnapshotParticipant<FluidVariant> implements SingleSlotStorage<FluidVariant>{

    private final SimpleFluidContainer handler;
    public SimpleTankStorageImpl(SimpleFluidContainer handler) {
        super();
        this.handler = handler;
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
            var stack = handler.getFluid();
            if (stack.isEmpty())
                return FluidVariant.blank();

            return FluidVariant.of(stack.getFluid(), stack.getComponents().asPatch());
        }

        @Override
        public long getAmount() {
            return handler.getFluid().getAmount();
        }

        @Override
        public boolean isResourceBlank() {
            return handler.getFluid().isEmpty();
        }

        /* ---------------- Fill ---------------- */

        @Override
        public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {

            if (resource.isBlank() || maxAmount <= 0)
                return 0;

            var existing = handler.getFluid();

            // Different fluid
            if (!existing.isEmpty() &&
                !FluidVariant.of(existing.getFluid(), existing.getComponents().asPatch()).equals(resource))
                return 0;

            long inserted = Math.min(maxAmount, SimpleFluidContainer.MAX_FLUID - existing.getAmount());

            if (inserted <= 0)
                return 0;

            updateSnapshots(transaction);

            if (existing.isEmpty()) {
                handler.setFluid(
                        FluidStack.create(
                                resource.getFluid(),
                                inserted
                        )
                );
            } else {
                existing.grow(inserted);
                handler.setFluid(existing);
            }

            updateSnapshots(transaction);
            return inserted;
        }

        /* ---------------- Drain ---------------- */

        @Override
        public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {

            var existing = handler.getFluid();
            if (existing.isEmpty())
                return 0;

            if (!FluidVariant.of(existing.getFluid(), existing.getComponents().asPatch()).equals(resource))
                return 0;

            long extracted = Math.min(maxAmount, existing.getAmount());

            updateSnapshots(transaction);


            handler.removeFluid( extracted);
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
                handler.setFluid(FluidStack.empty());
            } else {
                handler.setFluid(
                        FluidStack.create(
                                snapshot.getFluid(),
                                handler.getFluid().getAmount()
                        )
                );
            }
        }

}

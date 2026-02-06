package fr.iglee42.compressedbox.forge.implementations;

import dev.architectury.hooks.fluid.forge.FluidStackHooksForge;
import fr.iglee42.compressedbox.containers.fluids.ConnectedTankHandler;
import fr.iglee42.compressedbox.containers.fluids.SimpleFluidContainer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class ConnectedTankWrapper implements IFluidHandler {

    private final ConnectedTankHandler tanks;

    public ConnectedTankWrapper(ConnectedTankHandler tanks) {
        this.tanks = tanks;
    }

    @Override
    public int getTanks() {
        return tanks.getContainerSize();
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return FluidStackHooksForge.toForge(tanks.getFluid(tank));
    }

    @Override
    public int getTankCapacity(int tank) {
        return (int) SimpleFluidContainer.MAX_FLUID;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return true;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty())
            return 0;

        int filled = 0;

        for (int i = 0; i < getTanks(); i++) {

            FluidStack existing = getFluidInTank(i);

            if (existing.isEmpty()) {

                int toFill = Math.min(resource.getAmount(), getTankCapacity(i));

                if (action.execute()) {
                    FluidStack copy = resource.copy();
                    copy.setAmount(toFill);
                    tanks.setFluid(i, FluidStackHooksForge.fromForge(copy));
                    tanks.setChanged();
                }

                filled += toFill;
                return filled;
            }

            if (FluidStack.areFluidStackTagsEqual(existing, resource)) {

                int space = getTankCapacity(i) - existing.getAmount();
                if (space <= 0)
                    continue;

                int toFill = Math.min(space, resource.getAmount());

                if (action.execute()) {
                    existing.grow(toFill);
                    tanks.setFluid(i, FluidStackHooksForge.fromForge(existing));
                    tanks.setChanged();
                }

                filled += toFill;
                return filled;
            }
        }

        return filled;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty())
            return FluidStack.EMPTY;

        for (int i = 0; i < getTanks(); i++) {

            FluidStack existing = getFluidInTank(i);

            if (existing.isEmpty())
                continue;

            if (!FluidStack.areFluidStackTagsEqual(existing, resource))
                continue;

            int toDrain = Math.min(existing.getAmount(), resource.getAmount());

            if (action.execute()) {
                return FluidStackHooksForge.toForge(tanks.removeFluid(i, toDrain));
            } else {
                FluidStack copy = existing.copy();
                copy.setAmount(toDrain);
                return copy;
            }
        }

        return FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {

        if (maxDrain <= 0)
            return FluidStack.EMPTY;

        for (int i = 0; i < getTanks(); i++) {

            FluidStack existing = getFluidInTank(i);

            if (existing.isEmpty())
                continue;

            int toDrain = Math.min(existing.getAmount(), maxDrain);

            if (action.execute()) {
                return FluidStackHooksForge.toForge(tanks.removeFluid(i, toDrain));
            } else {
                FluidStack copy = existing.copy();
                copy.setAmount(toDrain);
                return copy;
            }
        }

        return FluidStack.EMPTY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConnectedTankWrapper other)) return false;
        return tanks.equals(other.tanks);
    }

    @Override
    public int hashCode() {
        return tanks.hashCode();
    }

    public ConnectedTankHandler getHandler() {
        return tanks;
    }
}

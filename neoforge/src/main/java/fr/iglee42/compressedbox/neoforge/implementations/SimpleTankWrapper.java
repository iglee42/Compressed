package fr.iglee42.compressedbox.neoforge.implementations;

import dev.architectury.hooks.fluid.forge.FluidStackHooksForge;
import fr.iglee42.compressedbox.containers.fluids.ConnectedTankHandler;
import fr.iglee42.compressedbox.containers.fluids.SimpleFluidContainer;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class SimpleTankWrapper implements IFluidHandler {

    private final SimpleFluidContainer tank;

    public SimpleTankWrapper(SimpleFluidContainer tank) {
        this.tank = tank;
    }

    @Override
    public int getTanks() {
        return (int) SimpleFluidContainer.MAX_FLUID;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return FluidStackHooksForge.toForge(this.tank.getFluid());
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


        FluidStack existing = getFluidInTank(0);

        if (existing.isEmpty()) {

            int toFill = Math.min(resource.getAmount(), getTankCapacity(0));

            if (action.execute()) {
                FluidStack copy = resource.copy();
                copy.setAmount(toFill);
                tank.setFluid(FluidStackHooksForge.fromForge(copy));
                tank.setChanged();
            }

            filled += toFill;
            return filled;
        }

        if (FluidStack.isSameFluidSameComponents(existing, resource)) {

            int space = getTankCapacity(0) - existing.getAmount();
            if (space <= 0)
                return filled;

            int toFill = Math.min(space, resource.getAmount());

            if (action.execute()) {
                existing.grow(toFill);
                tank.setFluid(FluidStackHooksForge.fromForge(existing));
                tank.setChanged();
            }

            filled += toFill;
            return filled;
        }

        return filled;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty())
            return FluidStack.EMPTY;

        FluidStack existing = getFluidInTank(0);

        if (existing.isEmpty())
            return FluidStack.EMPTY;

        if (!FluidStack.isSameFluidSameComponents(existing, resource))
            return FluidStack.EMPTY;

        int toDrain = Math.min(existing.getAmount(), resource.getAmount());

        if (action.execute()) {
            return FluidStackHooksForge.toForge(tank.removeFluid(toDrain));
        } else {
            FluidStack copy = existing.copy();
            copy.setAmount(toDrain);
            return copy;
        }

    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {

        if (maxDrain <= 0)
            return FluidStack.EMPTY;


        FluidStack existing = getFluidInTank(0);

        if (existing.isEmpty())
            return FluidStack.EMPTY;

        int toDrain = Math.min(existing.getAmount(), maxDrain);

        if (action.execute()) {
            return FluidStackHooksForge.toForge(tank.removeFluid(toDrain));
        } else {
            FluidStack copy = existing.copy();
            copy.setAmount(toDrain);
            return copy;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleTankWrapper other)) return false;
        return tank.equals(other.tank);
    }

    @Override
    public int hashCode() {
        return tank.hashCode();
    }

    public SimpleFluidContainer getHandler() {
        return tank;
    }
}

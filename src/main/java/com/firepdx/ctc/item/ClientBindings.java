package com.firepdx.ctc.item;

import net.minecraft.core.BlockPos;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Thin indirection so common-side code (item use logic, the lectern block entity) can
 * trigger client-only behavior (toggling the controller's active/bind overlay, notifying
 * the client controller handler of a lectern user change) without the common source set
 * depending on the client source set, which Loom's split source sets don't allow.
 * Populated from com.firepdx.ctc.client.CreateTweakedControllersClient at client init.
 */
public final class ClientBindings
{
    private ClientBindings() {}

    @FunctionalInterface
    public interface LecternUserChangedListener
    {
        void onChanged(BlockPos pos, UUID prevUser, UUID user);
    }

    public static Runnable toggleActive = () -> {};
    public static Consumer<BlockPos> toggleBindMode = pos -> {};
    public static LecternUserChangedListener onLecternUserChanged = (pos, prev, cur) -> {};
}

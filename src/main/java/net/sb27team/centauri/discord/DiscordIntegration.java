/*
 * Copyright (c) 2017-2018 SB27Team (superblaubeere27, Cubixy, Xc3pt1on, SplotyCode)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.sb27team.centauri.discord;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;

public class DiscordIntegration {
    private static DiscordRPC lib;
    private static Thread thread;

    public synchronized static void init() {
//        System.out.println("Init");

        if (!isEnabled()) {
//            System.out.println("Init stop A");
            stopRPC();
            return;
        }

        if (thread != null) {
            stopRPC();
        }

        lib = DiscordRPC.INSTANCE;
        String applicationId = "448060676926078996";
        String steamId = "";
        DiscordEventHandlers handlers = new DiscordEventHandlers();

        handlers.ready = user -> {
//                System.out.println("Discord RPC initialized");
            updateRPC(null, null);
        };

        lib.Discord_Initialize(applicationId, handlers, true, steamId);

        if (thread == null || !thread.isAlive()) {
            thread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    lib.Discord_RunCallbacks();

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ignored) {
                    }
                }
            }, "RPC-Callback-Handler");
            thread.start();
        }
    }

    private static boolean isEnabled() {
        return true;
    }

    public synchronized static void updateRPC(String fileName, String shownClass) {
        if (!isEnabled()) {
//            System.out.println("Update stop A");
            stopRPC();
            return;
        }
        if (thread == null) {
            init();
        }
//        System.out.println("Update " + fileName + " " + shownClass);

        DiscordRichPresence presence = new DiscordRichPresence();
        presence.startTimestamp = System.currentTimeMillis() / 1000;

        presence.details = fileName != null ? fileName : "Idle";
        presence.state = shownClass != null ? "Decompiling " + shownClass : "";
        presence.largeImageKey = "centauri";

        lib.Discord_UpdatePresence(presence);
    }

    public synchronized static void stopRPC() {
        if (thread != null) {
            thread.stop();
            thread = null;
            lib.Discord_ClearPresence();
            lib.Discord_Shutdown();
//            System.out.println("Stopping discord integration");
        }
    }

}
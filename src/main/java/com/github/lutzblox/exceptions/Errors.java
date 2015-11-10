package com.github.lutzblox.exceptions;

import com.github.lutzblox.Listenable;
import com.github.lutzblox.properties.Localization;


public class Errors {

    public static void threadErrored(String threadName, Listenable listenable) {

        NetworkException ex = getThreadErrored(threadName);

        if (listenable != null) {

            listenable.report(ex);

        } else {

            throw ex;
        }
    }

    public static void threadErrored(String threadName) {

        threadErrored(threadName, null);
    }

    public static NetworkException getThreadErrored(String threadName) {

        return new NetworkException(Localization.getMessage(Localization.THREAD_HAS_ERRORED, threadName));
    }

    public static void serverTimedOut(Listenable listenable) {

        NetworkException ex = getServerTimedOut();

        if (listenable != null) {

            listenable.report(ex);

        } else {

            throw ex;
        }
    }

    public static void serverTimedOut() {

        serverTimedOut(null);
    }

    public static NetworkException getServerTimedOut() {

        return new NetworkException(Localization.getMessage(Localization.SERVER_TIMEOUT));
    }

    public static void invalidPacketConstructor(Listenable listenable) {

        NetworkException ex = getInvalidPacketConstructor();

        if (listenable != null) {

            listenable.report(ex);

        } else {

            throw ex;
        }
    }

    public static void invalidPacketConstructor() {

        invalidPacketConstructor(null);
    }

    public static NetworkException getInvalidPacketConstructor() {

        return new NetworkException(Localization.getMessage(Localization.PACKET_CONSTRUCTOR));
    }

    public static void unreadablePacket(Listenable listenable) {

        NetworkException ex = getUnreadablePacket();

        if (listenable != null) {

            listenable.report(ex);

        } else {

            throw ex;
        }
    }

    public static void unreadablePacket() {

        unreadablePacket(null);
    }

    public static NetworkException getUnreadablePacket() {

        return new NetworkException(Localization.getMessage(Localization.UNREADABLE_PACKET));
    }
}

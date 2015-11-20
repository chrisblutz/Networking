package com.github.lutzblox.exceptions;

import com.github.lutzblox.Listenable;
import com.github.lutzblox.properties.Localization;


public class Errors {

    public static void threadErrored(String threadName, Listenable listenable, Throwable parent) {

        NetworkException ex = getThreadErrored(threadName, parent);

        if (listenable != null) {

            listenable.report(ex);

        } else {

            throw ex;
        }
    }

    public static void threadErrored(String threadName, Throwable parent) {

        threadErrored(threadName, null);
    }

    public static NetworkException getThreadErrored(String threadName, Throwable parent) {

        NetworkException exception = new NetworkException(Localization.getMessage(Localization.THREAD_HAS_ERRORED, threadName)+(parent != null ? " ("+Localization.getMessage(Localization.CAUSED_BY, parent.getClass().getName(), parent.getMessage())+")" : ""));

        if (parent != null) {

            exception.setStackTrace(parent.getStackTrace());
        }

        return exception;
    }

    public static void serverTimedOut(Listenable listenable, Throwable parent) {

        NetworkException ex = getServerTimedOut(parent);

        if (listenable != null) {

            listenable.report(ex);

        } else {

            throw ex;
        }
    }

    public static void serverTimedOut(Throwable parent) {

        serverTimedOut(null, parent);
    }

    public static NetworkException getServerTimedOut(Throwable parent) {

        NetworkException exception = new NetworkException(Localization.getMessage(Localization.SERVER_TIMEOUT)+(parent != null ? " ("+Localization.getMessage(Localization.CAUSED_BY, parent.getClass().getName(), parent.getMessage())+")" : ""));

        if (parent != null) {

            exception.setStackTrace(parent.getStackTrace());
        }

        return exception;
    }

    public static void invalidPacketConstructor(Listenable listenable, Throwable parent) {

        NetworkException ex = getInvalidPacketConstructor(parent);

        if (listenable != null) {

            listenable.report(ex);

        } else {

            throw ex;
        }
    }

    public static void invalidPacketConstructor(Throwable parent) {

        invalidPacketConstructor(null, parent);
    }

    public static NetworkException getInvalidPacketConstructor(Throwable parent) {

        NetworkException exception = new NetworkException(Localization.getMessage(Localization.PACKET_CONSTRUCTOR)+(parent != null ? " ("+Localization.getMessage(Localization.CAUSED_BY, parent.getClass().getName(), parent.getMessage())+")" : ""));

        if (parent != null) {

            exception.setStackTrace(parent.getStackTrace());
        }

        return exception;
    }

    public static void unreadablePacket(Listenable listenable, Throwable parent) {

        NetworkException ex = getUnreadablePacket(parent);

        if (listenable != null) {

            listenable.report(ex);

        } else {

            throw ex;
        }
    }

    public static void unreadablePacket(Throwable parent) {

        unreadablePacket(null, parent);
    }

    public static NetworkException getUnreadablePacket(Throwable parent) {

        NetworkException exception = new NetworkException(Localization.getMessage(Localization.UNREADABLE_PACKET)+(parent != null ? " ("+Localization.getMessage(Localization.CAUSED_BY, parent.getClass().getName(), parent.getMessage())+")" : ""));

        if (parent != null) {

            exception.setStackTrace(parent.getStackTrace());
        }

        return exception;
    }

    public static void encryptionFailed(Listenable listenable, String type, Throwable parent) {

        NetworkException ex = getEncryptionFailed(type, parent);

        if (listenable != null) {

            listenable.report(ex);

        } else {

            throw ex;
        }
    }

    public static void encryptionFailed(String type, Throwable parent) {

        encryptionFailed(null, type, parent);
    }

    public static NetworkException getEncryptionFailed(String type, Throwable parent) {

        NetworkException exception = new NetworkException(Localization.getMessage(Localization.ENCRYPTION_FAILED, type)+(parent != null ? " ("+Localization.getMessage(Localization.CAUSED_BY, parent.getClass().getName(), parent.getMessage())+")" : ""));

        if (parent != null) {

            exception.setStackTrace(parent.getStackTrace());
        }

        return exception;
    }

    public static void decryptionFailed(Listenable listenable, String type, Throwable parent) {

        NetworkException ex = getDecryptionFailed(type, parent);

        if (listenable != null) {

            listenable.report(ex);

        } else {

            throw ex;
        }
    }

    public static void decryptionFailed(String type, Throwable parent) {

        decryptionFailed(null, type, parent);
    }

    public static NetworkException getDecryptionFailed(String type, Throwable parent) {

        NetworkException exception = new NetworkException(Localization.getMessage(Localization.DECRYPTION_FAILED, type)+(parent != null ? " ("+Localization.getMessage(Localization.CAUSED_BY, parent.getClass().getName(), parent.getMessage())+")" : ""));

        if (parent != null) {

            exception.setStackTrace(parent.getStackTrace());
        }

        return exception;
    }
}

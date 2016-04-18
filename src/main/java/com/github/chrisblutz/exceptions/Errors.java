package com.github.chrisblutz.exceptions;

import com.github.chrisblutz.Listenable;
import com.github.chrisblutz.properties.Localization;


/**
 * A utility class for creating {@code Exceptions} based on localizations
 */
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

        NetworkException exception = new NetworkException(Localization.getMessage(Localization.THREAD_HAS_ERRORED, threadName) + (parent != null && !parent.getMessage().equals("") ? " (" + Localization.getMessage(Localization.CAUSED_BY, parent.getClass().getName(), parent.getMessage()) + ")" : ""));

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

        NetworkException exception = new NetworkException(Localization.getMessage(Localization.SERVER_TIMEOUT) + (parent != null && !parent.getMessage().equals("") ? " (" + Localization.getMessage(Localization.CAUSED_BY, parent.getClass().getName(), parent.getMessage()) + ")" : ""));

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

        NetworkException exception = new NetworkException(Localization.getMessage(Localization.PACKET_CONSTRUCTOR) + (parent != null && !parent.getMessage().equals("") ? " (" + Localization.getMessage(Localization.CAUSED_BY, parent.getClass().getName(), parent.getMessage()) + ")" : ""));

        if (parent != null) {

            exception.setStackTrace(parent.getStackTrace());
        }

        return exception;
    }

    public static void unreadableData(Listenable listenable, Throwable parent) {

        NetworkException ex = getUnreadableData(parent);

        if (listenable != null) {

            listenable.report(ex);

        } else {

            throw ex;
        }
    }

    public static void unreadableData(Throwable parent) {

        unreadableData(null, parent);
    }

    public static NetworkException getUnreadableData(Throwable parent) {

        NetworkException exception = new NetworkException(Localization.getMessage(Localization.UNREADABLE_DATA) + (parent != null && !parent.getMessage().equals("") ? " (" + Localization.getMessage(Localization.CAUSED_BY, parent.getClass().getName(), parent.getMessage()) + ")" : ""));

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

        NetworkException exception = new NetworkException(Localization.getMessage(Localization.ENCRYPTION_FAILED, type) + (parent != null && !parent.getMessage().equals("") ? " (" + Localization.getMessage(Localization.CAUSED_BY, parent.getClass().getName(), parent.getMessage()) + ")" : ""));

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

        NetworkException exception = new NetworkException(
                Localization.getMessage(Localization.DECRYPTION_FAILED, type)
                        + (parent != null && parent.getMessage() != null && !parent.getMessage().equals("")
                        ? " (" + Localization.getMessage(Localization.CAUSED_BY,
                        parent.getClass().getName(), parent.getMessage()) + ")"
                        : ""));

        if (parent != null) {

            exception.setStackTrace(parent.getStackTrace());
        }

        return exception;
    }
    
    public static void disallowedForcedStateChange(Listenable listenable, Throwable parent) {
        
        NetworkException ex = getDisallowedForcedStateChange(parent);
        
        if (listenable != null) {
            
            listenable.report(ex);
            
        } else {
            
            throw ex;
        }
    }
    
    public static void disallowedForcedStateChange(Throwable parent) {
        
        disallowedForcedStateChange(null, parent);
    }
    
    public static NetworkException getDisallowedForcedStateChange(Throwable parent) {
        
        NetworkException exception = new NetworkException(Localization.getMessage(Localization.DISALLOWED_FORCED_STATE_CHANGE) + (parent != null && !parent.getMessage().equals("") ? " (" + Localization.getMessage(Localization.CAUSED_BY, parent.getClass().getName(), parent.getMessage()) + ")" : ""));
        
        if (parent != null) {
            
            exception.setStackTrace(parent.getStackTrace());
        }
        
        return exception;
    }

    public static void missingDataType(Listenable listenable, String type, String name, Throwable parent) {

        NullPointerException ex = getMissingDataType(type, name, parent);

        if (listenable != null) {

            listenable.report(ex);

        } else {

            throw ex;
        }
    }

    public static void missingDataType(String type, String name, Throwable parent) {

        missingDataType(null, type, name, parent);
    }

    public static NullPointerException getMissingDataType(String type, String name, Throwable parent) {

        NullPointerException exception = new NullPointerException(Localization.getMessage(Localization.MISSING_DATA_TYPE, type, name) + (parent != null && !parent.getMessage().equals("") ? " (" + Localization.getMessage(Localization.CAUSED_BY, parent.getClass().getName(), parent.getMessage()) + ")" : ""));

        if (parent != null) {

            exception.setStackTrace(parent.getStackTrace());
        }

        return exception;
    }

    public static void noResponseExpected(Listenable listenable, String type, Throwable parent) {

        NetworkException ex = getNoResponseExpected(type, parent);

        if (listenable != null) {

            listenable.report(ex);

        } else {

            throw ex;
        }
    }

    public static void noResponseExpected(String type, Throwable parent) {

        noResponseExpected(null, type, parent);
    }

    public static NetworkException getNoResponseExpected(String type, Throwable parent) {

        NetworkException exception = new NetworkException(Localization.getMessage(Localization.NO_RESPONSE_EXPECTED, type) + (parent != null && !parent.getMessage().equals("") ? " (" + Localization.getMessage(Localization.CAUSED_BY, parent.getClass().getName(), parent.getMessage()) + ")" : ""));

        if (parent != null) {

            exception.setStackTrace(parent.getStackTrace());
        }

        return exception;
    }

    public static void responseTimedOut(Listenable listenable, String type, Throwable parent) {

        NetworkException ex = getResponseTimedOut(type, parent);

        if (listenable != null) {

            listenable.report(ex);

        } else {

            throw ex;
        }
    }

    public static void responseTimedOut(String type, Throwable parent) {

        responseTimedOut(null, type, parent);
    }

    public static NetworkException getResponseTimedOut(String type, Throwable parent) {

        NetworkException exception = new NetworkException(Localization.getMessage(Localization.RESPONSE_TIMED_OUT, type) + (parent != null && !parent.getMessage().equals("") ? " (" + Localization.getMessage(Localization.CAUSED_BY, parent.getClass().getName(), parent.getMessage()) + ")" : ""));

        if (parent != null) {

            exception.setStackTrace(parent.getStackTrace());
        }

        return exception;
    }

    public static void malformedQuery(Listenable listenable, String id, String type, Throwable parent) {

        NetworkException ex = getMalformedQuery(id, type, parent);

        if (listenable != null) {

            listenable.report(ex);

        } else {

            throw ex;
        }
    }

    public static void malformedQuery(String id, String type, Throwable parent) {

        malformedQuery(null, id, type, parent);
    }

    public static NetworkException getMalformedQuery(String id, String type, Throwable parent) {

        NetworkException exception = new NetworkException(Localization.getMessage(Localization.MALFORMED_QUERY, id, type) + (parent != null && !parent.getMessage().equals("") ? " (" + Localization.getMessage(Localization.CAUSED_BY, parent.getClass().getName(), parent.getMessage()) + ")" : ""));

        if (parent != null) {

            exception.setStackTrace(parent.getStackTrace());
        }

        return exception;
    }

    public static void genericFatalConnection(Listenable listenable, String ip, int port, Throwable parent) {

        NetworkException ex = getGenericFatalConnection(ip, port, parent);

        if (listenable != null) {

            listenable.report(ex);

        } else {

            throw ex;
        }
    }

    public static void genericFatalConnection(String ip, int port, Throwable parent) {

        genericFatalConnection(null, ip, port, parent);
    }

    public static NetworkException getGenericFatalConnection(String ip, int port, Throwable parent) {

        NetworkException exception = new NetworkException(Localization.getMessage(Localization.GENERIC_FATAL_CONNECTION, ip, Integer.toString(port)) + (parent != null && !parent.getMessage().equals("") ? " (" + Localization.getMessage(Localization.CAUSED_BY, parent.getClass().getName(), parent.getMessage()) + ")" : ""));

        if (parent != null) {

            exception.setStackTrace(parent.getStackTrace());
        }

        return exception;
    }

    public static void branchingNotServerSide(Listenable listenable, Throwable parent) {

        NetworkException ex = getBranchingNotServerSide(parent);

        if (listenable != null) {

            listenable.report(ex);

        } else {

            throw ex;
        }
    }

    public static void branchingNotServerSide(Throwable parent) {

        branchingNotServerSide(null, parent);
    }

    public static NetworkException getBranchingNotServerSide(Throwable parent) {

        NetworkException exception = new NetworkException(Localization.getMessage(Localization.BRANCHING_NOT_SERVER_SIDE) + (parent != null && !parent.getMessage().equals("") ? " (" + Localization.getMessage(Localization.CAUSED_BY, parent.getClass().getName(), parent.getMessage()) + ")" : ""));

        if (parent != null) {

            exception.setStackTrace(parent.getStackTrace());
        }

        return exception;
    }

    public static void branchingFailed(Listenable listenable, Throwable parent) {

        NetworkException ex = getBranchingFailed(parent);

        if (listenable != null) {

            listenable.report(ex);

        } else {

            throw ex;
        }
    }

    public static void branchingFailed(Throwable parent) {

        branchingFailed(null, parent);
    }

    public static NetworkException getBranchingFailed(Throwable parent) {

        NetworkException exception = new NetworkException(Localization.getMessage(Localization.BRANCHING_FAILED) + (parent != null && !parent.getMessage().equals("") ? " (" + Localization.getMessage(Localization.CAUSED_BY, parent.getClass().getName(), parent.getMessage()) + ")" : ""));

        if (parent != null) {

            exception.setStackTrace(parent.getStackTrace());
        }

        return exception;
    }
}

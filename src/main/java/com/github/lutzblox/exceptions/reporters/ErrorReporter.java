package com.github.lutzblox.exceptions.reporters;

/**
 * A class used to report errors from a {@code Listenable}
 *
 * @author Christopher Lutz
 */
public abstract class ErrorReporter {

    /**
     * Called to actually report the data after formatting is completed
     *
     * @param toReport The formatted message to report
     */
    protected abstract void report(String toReport);

    /**
     * Reports a {@code Throwable}
     *
     * @param t The {@code Throwable} to report
     */
    public void report(Throwable t) {

        String toReport = t.toString() + "\n";

        for (StackTraceElement e : t.getStackTrace()) {

            toReport += "    " + e.toString() + "\n";
        }

        if (t.getCause() != null) {

            reportCause(t.getCause(), toReport);
        }

        report(toReport.trim());
    }

    private void reportCause(Throwable t, String toReport) {

        toReport += "caused by " + t.toString() + "\n";

        for (StackTraceElement e : t.getStackTrace()) {

            toReport += "    " + e.toString() + "\n";
        }

        if (t.getCause() != null) {

            reportCause(t.getCause(), toReport);
        }
    }
}

package com.github.lutzblox.exceptions.reporters;

public abstract class ErrorReporter {

	protected abstract void report(String toReport);
	
	public void report(Throwable t){

		String toReport = t.toString()+"\n";

		for (StackTraceElement e : t.getStackTrace()) {

			toReport += "/t" + e.toString()+"\n";
		}
		
		if(t.getCause() != null){
			
			reportCause(t.getCause(), toReport);
		}
		
		report(toReport.trim());
	}
	
	private void reportCause(Throwable t, String toReport){

		toReport += "caused by "+t.toString()+"\n";

		for (StackTraceElement e : t.getStackTrace()) {

			toReport += "/t" + e.toString()+"\n";
		}
		
		if(t.getCause() != null){
			
			reportCause(t.getCause(), toReport);
		}
	}
}

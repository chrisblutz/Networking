<h1>LutzBlox's Simplified Networking Library (LSNL)</h1>
Travis CI Build Status: <a href="https://travis-ci.org/LutzBlox/Networking"><img src="https://travis-ci.org/LutzBlox/Networking.svg?branch=master" /></a>
<h2>Obtaining the Library</h2>
<p>If you want the most recent stable release build, click <a href="https://github.com/LutzBlox/Networking/releases">here</a> and download the most recent binaries.  To get the most recent version you'll have to download the source code and build it yourself.  To obtain the source code, click the <a href="https://github.com/LutzBlox/Networking/archive/master.zip">Download ZIP</a> button (or click that link).  Extract the ZIP file and you're ready to go.</p>
<h2>Building the Library</h3>
<p>LSNL uses the Gradle build management system, which streamlines the building process.  Below are instructions for building LSNL with Gradle for several operating systems.</p>
<h2></h2>
<h4>Windows</h4>
<p>Open the command prompt (cmd.exe) and navigate to the folder where LSNL's build.gradle file is located.  Then, execute the following:</p>
<pre>
gradlew build
</pre>
<p>The finished <code>.jar</code> files can be found in <code>/build/libs</code>, called <code>networking-X.X.X.jar</code>, <code>networking-X.X.X-sources.jar</code>, and <code>networking-X.X.X-javadoc.jar</code> for the compiled library, source code, and javadocs respectively.</p>
<p>If you have problems, consult the <a href="#troubleshooting">troubleshooting guide</a>.</p>
<h2></h2>
<h4>Unix/Linux</h4>
<p>Open Terminal and navigate to the folder where LSNL's build.gradle file is located.  Then, execute the following:</p>
<pre>
bash ./gradlew build
</pre>
<h2></h2>
<a name="troubleshooting"></a>
<h4>Troubleshooting</h4></a>
<p>For more information about an error, append <code>--stacktrace</code> to your build command.  This will print out the stack trace of the error and other information cooresponding to the build process.  For specific errors, see below:</p>
<ul>
    <li>
    <h5>Missing <code>tools.jar</code>:</h5>
    <p>This usually means there is no JDK installed on your system, or if there is, it is not installed correctly or the $JAVA_HOME environmental variable is not set correctly.  Make sure you have installed the JDK (Java Development Kit), not the JRE (Java Runtime Environment).  The JDK is the only one with the files Gradle needs.  If you have installed the JDK, make sure there is an environmental variable called JAVA_HOME and that it is set to the home directory of your JDK (<code>.../java/jdkX.X.X_XX/</code>).</p>
    </li>
</ul>
<i>If you encounter any other issues, please post them in the <a href="https://github.com/LutzBlox/Networking/issues">Issues</a> section.</i>
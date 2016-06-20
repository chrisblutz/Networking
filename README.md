![SNL Logo](https://raw.githubusercontent.com/chrisblutz/Networking/master/images/logo-name.png)
## Obtaining the Library ##
If you want the most recent stable release build, click [here](https://github.com/chrisblutz/Networking/releases) and download the most recent binaries.  To get the most recent version you'll have to download the source code and build it yourself.  To obtain the source code, click the [Download ZIP](https://github.com/chrisblutz/Networking/archive/master.zip) button (or click that link).  Extract the ZIP file and you're ready to go.
## Building the Library ##
The Simplified Networking Library (SNL) uses the Gradle build management system, which streamlines the building process.  Below are instructions for building SNL with Gradle for several operating systems.

-
#### Windows ####
Open the command prompt (`cmd.exe`) and navigate to the folder where SNL's `build.gradle` file is located.  Then, execute the following:
```batchfile
gradlew build
```
The finished `.jar` files can be found in `/build/libs`, called `networking-X.X.X.jar`, `networking-X.X.X-sources.jar`, and `networking-X.X.X-javadoc.jar` for the compiled library, source code, and JavaDocs respectively.
If you have problems, consult the [troubleshooting guide](#troubleshooting).

-
#### Unix/Linux ####
Open Terminal and navigate to the folder where SNL's `build.gradle` file is located.  Then, execute the following:
```shell
bash ./gradlew build
```
The finished `.jar` files can be found in `/build/libs`, called `networking-X.X.X.jar`, `networking-X.X.X-sources.jar`, and `networking-X.X.X-javadoc.jar` for the compiled library, source code, and JavaDocs respectively.
If you have problems, consult the [troubleshooting guide](#troubleshooting).
## Using the Library ##
Consult the [SNL Wiki](https://github.com/chrisblutz/Networking/wiki) for instructions and tutorials for using the library.
##Troubleshooting ##
For more information about an error, append `--stacktrace` to your build command.  This will print out the stack trace of the error and other information cooresponding to the build process.  For specific errors, see below:
 - **Missing `tools.jar`:**
    This usually means there is no JDK installed on your system, or if there is, it is not installed correctly or the JAVA_HOME environmental variable is not set correctly.  Make sure you have installed the JDK (Java Development Kit), not the JRE (Java Runtime Environment).  The JDK is the only one with the files Gradle needs.  If you have installed the JDK, make sure there is an environmental variable called JAVA_HOME and that it is set to the home directory of your JDK (`.../java/jdkX.X.X_XX/`).
_If you encounter any other issues, please post them in the [Issues](https://github.com/chrisblutz/Networking/issues) section._

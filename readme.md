#LogWrap - Making logs less pain in the ass

This little library's only purpose is to make logging more convenient.
Currently it does so by allowing you to en- and disable logging anywhere inside your code, which finally allows you to get rid of those logs in production apps once and for all.
Furthermore it allows you to directly log into files.

##Switching to LogWrap
Switching to LogWrap is a really easy task. Currently LogWrap is not present on MavenCentral, but most certainly will move there in the near future, which will it make even more easy to include it in your project.
To use LogWrap for now, download the .jar from the download page of the repository, move it into the `libs` folder of your project (next to src and res) and specify to build jars inside your build.gradle by adding:

```
dependencies {
    compile fileTree(dir: 'libs', include: '*.jar')
}

```

LogWrap is completely compatible with the default android log syntax. Therefore the only thing you need to do after adding it, is to change your import statement from `import android.util.Log` to `import at.flosch.logwrap.Log`. That's it! LogWrap is now set up!

##Using LogWrap to dis- and enable logging
By default all logs in LogWrap are disabled.
To enable logging use `Log.enable()` and to disable logging again use `Log.disable()`.
A good place to enable logging would be in the `onCreate()` method of your MainActivity. To make sure you only enable logging, when you are using a debug build, you could use something like this (only if you are using Gradle):

```
if (BuildConfig.DEBUG) {
   Log.enable();
} else {
   Log.disable();
}
```

Now logs are automatically disabled, when you are generating a release build.

##Using non-default syntax
Another feature of LogWrap is to provide the possibility to set a default log level. If no loglevel is explicitly specified (e.g. by using Log.d, Log.w,..) the default log level will be used (which is VERBOSE by default).

```
Log.log("TAG", "message");               //logs to VERBOSE
Log.setDefaultLogLevel(LogLevel.WARN);
Log.log("TAG", "message");               //logs to WARN

```

Furthermore you could also use the enum directly.

```
Log.setDefaultLogLevel(LogLevel.WARN);
Log.log(LogLevel.VERBOSE, "TAG", "message");  //still logs to VERBOSE
```

##Using LogWrap to log to files
LogWrap can also be used to log directly into files.
First you should specify a filepath, if you don't the default filepath will be used (the logs directory on your sd-card).
After that you can log into files by using either `Log.f` or `Log.logToFile`.

```
Log.f("logfile.txt", "request", request.toString());
Log.f("logfile.txt", "response", response.toString());	//don't worry, that it uses the same filename here, it will be seperated by time and header
Log.logToFile("logfile.txt", "body", body);		//this automatically calls the toString() method of body
```

A sample logfile might look like this:
```
request at 2015-04-28 16:09:58
================================================================== 
api/v1/profiles/b0be3265-c96e-11e4-91d0-0ae8dd0a5384/trainings/677
================================================================== 
response at 2015-04-28 16:09:58
================================================================== 
{"success":1,"error":0,"data":{"total_distance":"0.0","battery":"68","current_speed":"25.0","timestamp":"1429194481004","user_id":"b0be3265-c96e-11e4-91d0-0ae8dd0a5384","status":"caught","avg_pace":"21","total_time":"1801000"}}
================================================================== 
```
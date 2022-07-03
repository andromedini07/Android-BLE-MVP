package com.programmingdev.androidblemvp;

import android.app.Application;

import com.programmingdev.androidblemvp.dependencyService.DependencyService;
import com.programmingdev.androidblemvp.dependencyService.IDependencyService;

// Todo Dependency Injection - Dagger 2
// Todo DataBinding
// Todo Code cleanup
// Todo Update SDK to Android S

// Custom Application class that needs to be specified
// in the AndroidManifest.xml file
public class MyApplication extends Application {
    // Instance of AppContainer that will be used by all the Activities of the app
    public IDependencyService dependencyService = new DependencyService();
}
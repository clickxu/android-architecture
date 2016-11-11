package com.example.android.architecture.blueprints.todoapp.util.schedulers;

import dagger.Module;
import dagger.Provides;

/**
 * This is a Dagger module. We use this to pass in the BaseSchedulerProvider dependency to the
 * {@link
 * com.example.android.architecture.blueprints.todoapp.util.schedulers.SchedulerProviderComponent}.
 */
@Module
public class SchedulerProviderModule {

    @Provides
    BaseSchedulerProvider provideSchedulerProvider() {
        return SchedulerProvider.getInstance();
    }
}

package com.moto.tracker.data.worker;

import androidx.hilt.work.WorkerAssistedFactory;
import androidx.work.ListenableWorker;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import javax.annotation.processing.Generated;

@Generated("androidx.hilt.AndroidXHiltProcessor")
@Module
@InstallIn(SingletonComponent.class)
@OriginatingElement(
    topLevelClass = DailyKmReminderWorker.class
)
public interface DailyKmReminderWorker_HiltModule {
  @Binds
  @IntoMap
  @StringKey("com.moto.tracker.data.worker.DailyKmReminderWorker")
  WorkerAssistedFactory<? extends ListenableWorker> bind(
      DailyKmReminderWorker_AssistedFactory factory);
}

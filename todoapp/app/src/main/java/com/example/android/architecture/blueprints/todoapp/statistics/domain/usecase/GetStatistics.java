package com.example.android.architecture.blueprints.todoapp.statistics.domain.usecase;

import android.support.annotation.NonNull;

import com.example.android.architecture.blueprints.todoapp.RxUseCase;
import com.example.android.architecture.blueprints.todoapp.SimpleUseCase;
import com.example.android.architecture.blueprints.todoapp.data.Task;
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository;
import com.example.android.architecture.blueprints.todoapp.statistics.domain.model.Statistics;
import com.example.android.architecture.blueprints.todoapp.util.schedulers.BaseSchedulerProvider;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Calculate statistics of active and completed Tasks {@link Task} in the {@link TasksRepository}.
 */
public class GetStatistics extends SimpleUseCase<GetStatistics.RequestValues, GetStatistics.ResponseValues> {

    private final TasksRepository mTasksRepository;

    @Inject
    public GetStatistics(@NonNull TasksRepository tasksRepository,
                         @NonNull BaseSchedulerProvider schedulerProvider) {
        super(schedulerProvider.io(), schedulerProvider.ui());
        mTasksRepository = tasksRepository;
    }

    @Override
    public Observable<ResponseValues> buildUseCase(RequestValues requestValues) {
        return mTasksRepository.getTasks().map(new Func1<List<Task>, ResponseValues>() {
            @Override
            public ResponseValues call(List<Task> tasks) {
                int activeTasks = 0;
                int completedTasks = 0;

                // We calculate number of active and completed tasks
                for (Task task : tasks) {
                    if (task.isCompleted()) {
                        completedTasks += 1;
                    } else {
                        activeTasks += 1;
                    }
                }
                return new ResponseValues(new Statistics(completedTasks, activeTasks));
            }
        });
    }

    public static class RequestValues implements RxUseCase.RequestValues {
    }

    public static class ResponseValues implements RxUseCase.ResponseValues {

        private final Statistics mStatistics;

        public ResponseValues(@NonNull Statistics statistics) {
            mStatistics = checkNotNull(statistics, "statistics cannot be null!");
        }

        public Statistics getStatistics() {
            return mStatistics;
        }
    }
}

/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.architecture.blueprints.todoapp.tasks.domain.usecase;

import android.support.annotation.NonNull;

import com.example.android.architecture.blueprints.todoapp.CompletableUseCase;
import com.example.android.architecture.blueprints.todoapp.RxUseCase;
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository;
import com.example.android.architecture.blueprints.todoapp.util.schedulers.BaseSchedulerProvider;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Marks a task as completed.
 */
public class CompleteTask extends CompletableUseCase<CompleteTask.RequestValues> {

    private final TasksRepository mTasksRepository;

    @Inject
    public CompleteTask(@NonNull TasksRepository tasksRepository,
                        @NonNull BaseSchedulerProvider schedulerProvider) {
        super(schedulerProvider.io(), schedulerProvider.ui());
        mTasksRepository = checkNotNull(tasksRepository, "tasksRepository cannot be null!");
    }

    @Override
    protected void complete(RequestValues values) {
        final String completedTask = values.getCompletedTask();
        mTasksRepository.completeTask(completedTask);
    }

    public static final class RequestValues implements RxUseCase.RequestValues {

        private final String mCompletedTask;

        public RequestValues(@NonNull String completedTask) {
            mCompletedTask = checkNotNull(completedTask, "completedTask cannot be null!");
        }

        public String getCompletedTask() {
            return mCompletedTask;
        }
    }
}

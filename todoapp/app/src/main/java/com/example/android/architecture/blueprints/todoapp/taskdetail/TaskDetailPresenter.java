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

package com.example.android.architecture.blueprints.todoapp.taskdetail;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.architecture.blueprints.todoapp.RxUseCase;
import com.example.android.architecture.blueprints.todoapp.addedittask.domain.usecase.DeleteTask;
import com.example.android.architecture.blueprints.todoapp.addedittask.domain.usecase.GetTask;
import com.example.android.architecture.blueprints.todoapp.data.Task;
import com.example.android.architecture.blueprints.todoapp.tasks.domain.usecase.ActivateTask;
import com.example.android.architecture.blueprints.todoapp.tasks.domain.usecase.CompleteTask;
import com.google.common.base.Strings;

import rx.Subscriber;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Listens to user actions from the UI ({@link TaskDetailFragment}), retrieves the data and updates
 * the UI as required.
 */
public class TaskDetailPresenter implements TaskDetailContract.Presenter {

    private final TaskDetailContract.View mTaskDetailView;
    private final GetTask mGetTask;
    private final CompleteTask mCompleteTask;
    private final ActivateTask mActivateTask;
    private final DeleteTask mDeleteTask;

    @Nullable
    private String mTaskId;

    public TaskDetailPresenter(@Nullable String taskId,
            @NonNull TaskDetailContract.View taskDetailView,
            @NonNull GetTask getTask,
            @NonNull CompleteTask completeTask,
            @NonNull ActivateTask activateTask,
            @NonNull DeleteTask deleteTask) {
        mTaskId = taskId;
        mTaskDetailView = checkNotNull(taskDetailView, "taskDetailView cannot be null!");
        mGetTask = checkNotNull(getTask, "getTask cannot be null!");
        mCompleteTask = checkNotNull(completeTask, "completeTask cannot be null!");
        mActivateTask = checkNotNull(activateTask, "activateTask cannot be null!");
        mDeleteTask = checkNotNull(deleteTask, "deleteTask cannot be null!");
        mTaskDetailView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        openTask();
    }

    @Override
    public void unsubscribe() {
        mGetTask.unsubscribe();
        mCompleteTask.unsubscribe();
        mActivateTask.unsubscribe();
        mDeleteTask.unsubscribe();
    }

    private void openTask() {
        if (Strings.isNullOrEmpty(mTaskId)) {
            mTaskDetailView.showMissingTask();
            return;
        }

        mTaskDetailView.setLoadingIndicator(true);

        mGetTask.execute(new GetTask.RequestValues(mTaskId), new Subscriber<GetTask.ResponseValues>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                // The view may not be able to handle UI updates anymore
                if (!mTaskDetailView.isActive()) {
                    return;
                }
                mTaskDetailView.showMissingTask();
            }

            @Override
            public void onNext(GetTask.ResponseValues response) {
                Task task = response.getTask();

                // The view may not be able to handle UI updates anymore
                if (!mTaskDetailView.isActive()) {
                    return;
                }
                mTaskDetailView.setLoadingIndicator(false);
                showTask(task);
            }
        });
    }

    @Override
    public void editTask() {
        if (Strings.isNullOrEmpty(mTaskId)) {
            mTaskDetailView.showMissingTask();
            return;
        }
        mTaskDetailView.showEditTask(mTaskId);
    }

    @Override
    public void deleteTask() {
        mDeleteTask.execute(new DeleteTask.RequestValues(mTaskId),
                new Subscriber<RxUseCase.NoResponseValues>() {
            @Override
            public void onCompleted() {
                mTaskDetailView.showTaskDeleted();
            }

            @Override
            public void onError(Throwable e) {
                // Show error, log, etc.
            }

            @Override
            public void onNext(RxUseCase.NoResponseValues noResponseValues) {

            }
        });
    }

    @Override
    public void completeTask() {
        if (Strings.isNullOrEmpty(mTaskId)) {
            mTaskDetailView.showMissingTask();
            return;
        }
        mCompleteTask.execute(new CompleteTask.RequestValues(mTaskId),
                new Subscriber<RxUseCase.NoResponseValues>() {
            @Override
            public void onCompleted() {
                mTaskDetailView.showTaskMarkedComplete();
            }

            @Override
            public void onError(Throwable e) {
                // Show error, log, etc.
            }

            @Override
            public void onNext(RxUseCase.NoResponseValues noResponseValues) {

            }
        });
    }

    @Override
    public void activateTask() {
        if (Strings.isNullOrEmpty(mTaskId)) {
            mTaskDetailView.showMissingTask();
            return;
        }
        mActivateTask.execute(new ActivateTask.RequestValues(mTaskId),
                new Subscriber<RxUseCase.NoResponseValues>() {
            @Override
            public void onCompleted() {
                mTaskDetailView.showTaskMarkedActive();
            }

            @Override
            public void onError(Throwable e) {
                // Show error, log, etc.
            }

            @Override
            public void onNext(RxUseCase.NoResponseValues noResponseValues) {

            }
        });
    }

    private void showTask(@NonNull Task task) {
        String title = task.getTitle();
        String description = task.getDescription();

        if (Strings.isNullOrEmpty(title)) {
            mTaskDetailView.hideTitle();
        } else {
            mTaskDetailView.showTitle(title);
        }

        if (Strings.isNullOrEmpty(description)) {
            mTaskDetailView.hideDescription();
        } else {
            mTaskDetailView.showDescription(description);
        }
        mTaskDetailView.showCompletionStatus(task.isCompleted());
    }
}

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

package com.example.android.architecture.blueprints.todoapp.addedittask;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.architecture.blueprints.todoapp.addedittask.domain.usecase.GetTask;
import com.example.android.architecture.blueprints.todoapp.addedittask.domain.usecase.SaveTask;
import com.example.android.architecture.blueprints.todoapp.data.Task;

import javax.inject.Inject;

import rx.Subscriber;

/**
 * Listens to user actions from the UI ({@link AddEditTaskFragment}), retrieves the data and
 * updates
 * the UI as required.
 */
public class AddEditTaskPresenter implements AddEditTaskContract.Presenter {

    private final AddEditTaskContract.View mAddTaskView;

    private final GetTask mGetTask;

    private final SaveTask mSaveTask;

    @Nullable
    private String mTaskId;

    /**
     * Dagger strictly enforces that arguments not marked with {@code @Nullable} are not injected
     * with {@code @Nullable} values.
     */
    @Inject
    public AddEditTaskPresenter(@Nullable String taskId,
                                @NonNull AddEditTaskContract.View addTaskView,
                                @NonNull GetTask getTask, @NonNull SaveTask saveTask) {
        mTaskId = taskId;
        mAddTaskView = addTaskView;
        mGetTask = getTask;
        mSaveTask = saveTask;
    }

    /**
     * Method injection is used here to safely reference {@code this} after the object is created.
     * For more information, see Java Concurrency in Practice.
     */
    @Inject
    void setupListeners() {
        mAddTaskView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        if (mTaskId != null) {
            populateTask();
        }
    }

    @Override
    public void unsubscribe() {
        mGetTask.unsubscribe();
        mSaveTask.unsubscribe();
    }

    @Override
    public void saveTask(String title, String description) {
        if (isNewTask()) {
            createTask(title, description);
        } else {
            updateTask(title, description);
        }
    }

    @Override
    public void populateTask() {
        if (mTaskId == null) {
            throw new RuntimeException("populateTask() was called but task is new.");
        }
        mGetTask.execute(new GetTask.RequestValues(mTaskId), new Subscriber<GetTask.ResponseValues>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                showEmptyTaskError();
            }

            @Override
            public void onNext(GetTask.ResponseValues response) {
                showTask(response.getTask());
            }
        });
    }

    private void showTask(Task task) {
        // The view may not be able to handle UI updates anymore
        if (mAddTaskView.isActive()) {
            mAddTaskView.setTitle(task.getTitle());
            mAddTaskView.setDescription(task.getDescription());
        }
    }

    private void showSaveError() {
        // Show error, log, etc.
    }

    private void showEmptyTaskError() {
        // The view may not be able to handle UI updates anymore
        if (mAddTaskView.isActive()) {
            mAddTaskView.showEmptyTaskError();
        }
    }

    private boolean isNewTask() {
        return mTaskId == null;
    }

    private void createTask(String title, String description) {
        Task newTask = new Task(title, description);
        if (newTask.isEmpty()) {
            mAddTaskView.showEmptyTaskError();
        } else {
            mSaveTask.execute(new SaveTask.RequestValues(newTask), new Subscriber<SaveTask.ResponseValues>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    showSaveError();
                }

                @Override
                public void onNext(SaveTask.ResponseValues responseValues) {
                    mAddTaskView.showTasksList();
                }
            });
        }
    }

    private void updateTask(String title, String description) {
        if (mTaskId == null) {
            throw new RuntimeException("updateTask() was called but task is new.");
        }
        Task newTask = new Task(title, description, mTaskId);
        mSaveTask.execute(new SaveTask.RequestValues(newTask), new Subscriber<SaveTask.ResponseValues>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                showSaveError();
            }

            @Override
            public void onNext(SaveTask.ResponseValues responseValues) {
                // After an edit, go back to the list.
                mAddTaskView.showTasksList();
            }
        });
    }
}

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

package com.example.android.architecture.blueprints.todoapp.statistics;

import android.support.annotation.NonNull;

import com.example.android.architecture.blueprints.todoapp.statistics.domain.model.Statistics;
import com.example.android.architecture.blueprints.todoapp.statistics.domain.usecase.GetStatistics;

import rx.Subscriber;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Listens to user actions from the UI ({@link StatisticsFragment}), retrieves the data and updates
 * the UI as required.
 */
public class StatisticsPresenter implements StatisticsContract.Presenter {

    private final StatisticsContract.View mStatisticsView;
    private final GetStatistics mGetStatistics;

    public StatisticsPresenter(
            @NonNull StatisticsContract.View statisticsView,
            @NonNull GetStatistics getStatistics) {
        mStatisticsView = checkNotNull(statisticsView, "StatisticsView cannot be null!");
        mGetStatistics = checkNotNull(getStatistics,"getStatistics cannot be null!");

        mStatisticsView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        loadStatistics();
    }

    @Override
    public void unsubscribe() {
        mGetStatistics.unsubscribe();
    }

    private void loadStatistics() {
        mStatisticsView.setProgressIndicator(true);
        mGetStatistics.execute(new GetStatistics.RequestValues(),
                new Subscriber<GetStatistics.ResponseValues>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                // The view may not be able to handle UI updates anymore
                if (!mStatisticsView.isActive()) {
                    return;
                }
                mStatisticsView.showLoadingStatisticsError();
            }

            @Override
            public void onNext(GetStatistics.ResponseValues response) {
                Statistics statistics = response.getStatistics();
                // The view may not be able to handle UI updates anymore
                if (!mStatisticsView.isActive()) {
                    return;
                }
                mStatisticsView.setProgressIndicator(false);

                mStatisticsView.showStatistics(statistics.getActiveTasks(), statistics.getCompletedTasks());
            }
        });
    }
}

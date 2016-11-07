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

package com.example.android.architecture.blueprints.todoapp;

import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class SimpleUseCase<Q extends RxUseCase.RequestValues,
        P extends RxUseCase.ResponseValues> extends RxUseCase<Q, P> {

    protected Scheduler mSubscribeOn;
    protected Scheduler mObserveOn;
    protected Subscription mSubscription;

    public SimpleUseCase(Scheduler subscribeOn, Scheduler observeOn) {
        mSubscribeOn = checkNotNull(subscribeOn, "subscribeOn cannot be null!");
        mObserveOn = checkNotNull(observeOn, "observeOn cannot be null!");
        mSubscription = Subscriptions.empty();
    }

    public void execute(Q requestValues, Subscriber<P> subscriber) {
        unsubscribe();
        mSubscription = buildUseCase(requestValues)
                .subscribeOn(mSubscribeOn)
                .observeOn(mObserveOn)
                .subscribe(subscriber);
    }

    public void unsubscribe() {
        if (!mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }
}

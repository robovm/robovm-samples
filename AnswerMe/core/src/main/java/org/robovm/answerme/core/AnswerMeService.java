/*
 * Copyright (C) 2015 RoboVM AB
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
package org.robovm.answerme.core;

import java.util.List;

import org.robovm.answerme.core.api.InstantAnswerAPI;
import org.robovm.answerme.core.api.Result;
import org.robovm.answerme.core.api.Topic;

import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * {@link AnswerMeService} encapsulates the <a
 * href="https://duckduckgo.com/api">DuckDuckGo Instant Answer API</a>.
 */
public class AnswerMeService {

    private final InstantAnswerAPI api;

    /**
     * Creates a new instance.
     */
    public AnswerMeService() {
        api = new Retrofit.Builder()
                .baseUrl("http://api.duckduckgo.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(InstantAnswerAPI.class);
    }

    /**
     * Asynchronously runs a query for topic summaries.
     * 
     * @param query the query to run.
     * @param onSuccess {@link Callback} which will be run when a result is
     *            returned successfully.
     * @param onFailure {@link Callback} which will be run on failure.
     */
    public void search(String query, final Callback<List<Topic>> onSuccess,
            final Callback<Throwable> onFailure) {

        api.search(query).enqueue(new retrofit.Callback<Result>() {
            @Override
            public void onResponse(Response<Result> response, Retrofit retrofit) {
                onSuccess.call(response.body().getTopics());
            }

            @Override
            public void onFailure(Throwable t) {
                onFailure.call(t);
            }
        });
    }
}

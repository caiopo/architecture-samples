/*
 * Copyright (C) 2015 The Android Open Source Project
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

package com.example.android.architecture.blueprints.todoapp

import android.content.Context
import android.net.ConnectivityManager
import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import com.example.android.architecture.blueprints.todoapp.data.source.local.TasksLocalDataSource
import com.example.android.architecture.blueprints.todoapp.data.source.local.ToDoDatabase
import com.example.android.architecture.blueprints.todoapp.data.source.remote.TasksRemoteDataSource
import com.example.android.architecture.blueprints.todoapp.data.source.remote.TasksService
import com.example.android.architecture.blueprints.todoapp.util.AppExecutors
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Enables injection of production implementations for
 * [TasksDataSource] at compile time.
 */
object Injection {
    fun provideTasksRepository(context: Context): TasksRepository {
        val appExecutors = AppExecutors()

        val database = ToDoDatabase.getInstance(context)
        val localDataSource = TasksLocalDataSource.getInstance(appExecutors, database.taskDao())

        val tasksService = provideTaskService()
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val remoteDataSource = TasksRemoteDataSource.getInstance(appExecutors, tasksService, connectivityManager)

        return TasksRepository.getInstance(
                remoteDataSource,
                localDataSource
        )
    }

    private fun provideTaskService(): TasksService {
        val retrofit = Retrofit.Builder()
                .baseUrl("http://tasks-api.wplexservices.com.br/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        return retrofit.create<TasksService>(TasksService::class.java)
    }
}

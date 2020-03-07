/*
 * Copyright 2017, The Android Open Source Project
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
package com.example.android.architecture.blueprints.todoapp.data.source.remote

import android.net.ConnectivityManager
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.core.net.ConnectivityManagerCompat
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource
import com.example.android.architecture.blueprints.todoapp.util.AppExecutors

/**
 * Implementation of the data source that fetches tasks from a remote server.
 */
class TasksRemoteDataSource private constructor(
        private val appExecutors: AppExecutors,
        private val tasksService: TasksService,
        private val connectivityManager: ConnectivityManager
) : TasksDataSource {

    override fun getTasks(callback: TasksDataSource.LoadTasksCallback) {
        if (ConnectivityManagerCompat.isActiveNetworkMetered(connectivityManager)) {
            callback.onDataNotAvailable()
            return
        }

        appExecutors.networkIO.execute {
            val request = tasksService.fetchTasks().execute()
            val body = request.body()

            appExecutors.mainThread.execute {
                if (request.isSuccessful && body != null) {
                    callback.onTasksLoaded(body)
                } else {
                    callback.onDataNotAvailable()
                }
            }
        }
    }

    override fun getTask(taskId: String, callback: TasksDataSource.GetTaskCallback) {}

    override fun saveTask(task: Task) {}

    override fun completeTask(task: Task) {}

    override fun completeTask(taskId: String) {}

    override fun activateTask(task: Task) {}

    override fun activateTask(taskId: String) {}

    override fun clearCompletedTasks() {}

    override fun refreshTasks() {}

    override fun deleteAllTasks() {}

    override fun deleteTask(taskId: String) {}

    companion object {
        private var INSTANCE: TasksRemoteDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors, tasksService: TasksService, connectivityManager: ConnectivityManager): TasksRemoteDataSource {
            if (INSTANCE == null) {
                synchronized(TasksRemoteDataSource::javaClass) {
                    INSTANCE = TasksRemoteDataSource(appExecutors, tasksService, connectivityManager)
                }
            }
            return INSTANCE!!
        }

        @VisibleForTesting
        fun clearInstance() {
            INSTANCE = null
        }
    }
}
package com.example.android.architecture.blueprints.todoapp.data.source.remote

import com.example.android.architecture.blueprints.todoapp.data.Task
import retrofit2.Call
import retrofit2.http.GET

interface TasksService {
    @GET("/tasks")
    fun fetchTasks(): Call<List<Task>>
}

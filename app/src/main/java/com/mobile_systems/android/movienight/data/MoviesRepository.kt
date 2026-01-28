/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mobile_systems.android.movienight.data

import com.mobile_systems.android.movienight.data.network.MoviesApiService
import com.mobile_systems.android.movienight.model.Movie
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlin.collections.List


/**
 * Repository that fetch mars photos list from marsApi.
 */
interface MoviesRepository {

    suspend fun getMovie(id:String) : Movie
}

/**
 * Network Implementation of Repository that fetch mars photos list from marsApi.
 */
class NetworkMoviesRepository(
    private val moviesApiService: MoviesApiService
) : MoviesRepository {
    override suspend fun getMovie(id: String): Movie {
        return moviesApiService.getMovie(id)
    }
}

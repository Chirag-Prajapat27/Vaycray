package com.halfeaten.vaycray.data.remote.paging.reviews

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.apollographql.apollo3.ApolloClient
import com.halfeaten.vaycray.GetPendingUserReviewsQuery
import com.halfeaten.vaycray.GetUserReviewsQuery
import java.util.concurrent.Executor

class PendingReviewsListDataSourceFactory(
    private val apolloClient: ApolloClient,
    private val query: GetPendingUserReviewsQuery,
    private val executor: Executor) : DataSource.Factory<String, GetPendingUserReviewsQuery.Result>() {
    val sourceLiveData = MutableLiveData<PagedKeyPendingReviewsListingSource>()
    override fun create(): DataSource<String, GetPendingUserReviewsQuery.Result> {
        val source = PagedKeyPendingReviewsListingSource(apolloClient, query, executor)
        sourceLiveData.postValue(source)
        return source
    }
}
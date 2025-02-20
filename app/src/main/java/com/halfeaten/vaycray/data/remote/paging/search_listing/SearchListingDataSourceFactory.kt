package com.halfeaten.vaycray.data.remote.paging.search_listing

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.apollographql.apollo3.ApolloClient
import com.halfeaten.vaycray.SearchListingQuery
import java.util.concurrent.Executor

class SearchListingDataSourceFactory(
    private val apolloClient: ApolloClient,
    private val query: SearchListingQuery,
    private val executor: Executor
) : DataSource.Factory<String, SearchListingQuery.Result>() {
    val sourceLiveData = MutableLiveData<PageKeyedSearchListingSource>()
    override fun create(): DataSource<String, SearchListingQuery.Result> {
        val source = PageKeyedSearchListingSource(apolloClient, query, executor)
        sourceLiveData.postValue(source)
        return source
    }
}
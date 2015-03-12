package me.gerbit.twitter.api;

import android.test.AndroidTestCase;

public class SearchQueryTest extends AndroidTestCase {
    public void testSearchQueryBuilder() {
        SearchQuery query = SearchQuery.builder("#android")
                .lang("ru")
                .resultType(SearchQuery.ResultType.POPULAR)
                .build();
        assertEquals("?q=%23android&lang=ru&result_type=popular", query.toString());
    }
}

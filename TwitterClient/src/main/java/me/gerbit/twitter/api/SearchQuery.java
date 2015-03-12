package me.gerbit.twitter.api;

import android.net.Uri;

public class SearchQuery {

    public enum ResultType {
        MIXED("mixed"),
        RECENT("resent"),
        POPULAR("popular");

        private final String mValue;

        private ResultType(final String value) {
            mValue = value;
        }

        @Override
        public String toString() {
            return mValue;
        }
    }

    private final String mSearchQuery;

    private SearchQuery(Builder b) {
        Uri.Builder uriBuilder = new Uri.Builder();
        mSearchQuery = uriBuilder.appendQueryParameter("q", b.mSearchQuery)
                .appendQueryParameter("lang", b.mLang)
                .appendQueryParameter("result_type", b.mResultType.toString())
                .build().toString();
    }

    public static Builder builder(String searchQuery) {
        return new Builder(searchQuery);
    }

    @Override
    public String toString() {
        return mSearchQuery;
    }

    public static final class Builder {

        private final String mSearchQuery;

        private String mLang;

        private ResultType mResultType;

        private Builder(String searchQuery) {
            mSearchQuery = searchQuery;
        }

        public Builder lang(String lang) {
            mLang = lang;
            return this;
        }

        public Builder resultType(ResultType resultType) {
            mResultType = resultType;
            return this;
        }

        public SearchQuery build() {
            return new SearchQuery(this);
        }
    }
}

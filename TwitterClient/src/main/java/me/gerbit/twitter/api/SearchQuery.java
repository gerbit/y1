package me.gerbit.twitter.api;

import android.net.Uri;

import java.util.Locale;

public class SearchQuery {

    public enum ResultType {
        MIXED("mixed"),
        RECENT("resent"),
        POPULAR("popular");

        private final String mValue;

        ResultType(final String value) {
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
        uriBuilder.appendQueryParameter("q", b.mSearchQuery);
        if (b.mLang != null) {
            uriBuilder.appendQueryParameter("lang", b.mLang);
        }
        if (b.mResultType != null) {
            uriBuilder.appendQueryParameter("result_type", b.mResultType.toString());
        }
        mSearchQuery = uriBuilder.build().toString();
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
            if (searchQuery == null) {
                throw new IllegalArgumentException("search string cannot be empty");
            }
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

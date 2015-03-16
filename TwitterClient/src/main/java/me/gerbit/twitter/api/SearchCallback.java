package me.gerbit.twitter.api;

public interface SearchCallback {
    void onQueryComplete(SearchResponse searchResponse);
    void onError(int code, String msg);
}

package me.gerbit.twitter.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import me.gerbit.twitter.api.SearchCallback;
import me.gerbit.twitter.api.SearchQuery;
import me.gerbit.twitter.api.SearchResponse;
import me.gerbit.twitter.api.TwitterSearch;
import me.gerbit.twitter.data.SearchMetadata;
import me.gerbit.twitter.data.Tweet;

public class MainActivityFragment extends Fragment implements AbsListView.OnScrollListener {

    private static final String TAG = MainActivityFragment.class.getSimpleName();

    private static final int QUERY_COMPLETE = 0;
    private static final int NEXT_LOADED = 1;
    private static final int REFRESH = 2;

    private ListView mListView;

    private UiHandler mUiHandler;

    private TweetsAdapter mTweetsAdapter;

    private SearchMetadata mSearchMetadata;

    private TwitterSearch mTwitterSearch;

    private View mFooterView;

    private boolean mQueryInProgress;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mUiHandler = new UiHandler(this);

        mTwitterSearch = new TwitterSearch("TwitterSearchCaller");

        mTweetsAdapter = new TweetsAdapter(activity);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        mListView = (ListView) v.findViewById(R.id.list);
        mFooterView = inflater.inflate(R.layout.tweets_list_footer, null, false);
        mFooterView.setVisibility(View.GONE);
        mListView.addFooterView(mFooterView);
        mListView.setAdapter(mTweetsAdapter);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

        SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (mQueryInProgress) {
                    return true;
                }
                mQueryInProgress = true;
                mTwitterSearch.search(SearchQuery.builder(query).resultType(SearchQuery.ResultType.RECENT).lang("ru").build(), new SearchCallback() {
                    @Override
                    public void onQueryComplete(SearchResponse searchResponse) {
                        mQueryInProgress = false;
                        mUiHandler.obtainMessage(QUERY_COMPLETE, searchResponse).sendToTarget();
                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                if (mQueryInProgress) {
                    return true;
                }
                mQueryInProgress = true;
                mTwitterSearch.search(mSearchMetadata.getRefreshUrl(), new SearchCallback() {
                    @Override
                    public void onQueryComplete(SearchResponse searchResponse) {
                        mQueryInProgress = false;
                        mUiHandler.obtainMessage(REFRESH, searchResponse).sendToTarget();
                    }
                });
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        mTwitterSearch.quit();
        super.onDetach();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // nothing to do
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem == 0 || visibleItemCount == 0) {
            mFooterView.setVisibility(View.GONE);
            return;
        }
        if (firstVisibleItem + visibleItemCount == totalItemCount && !mQueryInProgress) {
            mQueryInProgress = true;
            mFooterView.setVisibility(View.VISIBLE);
            mTwitterSearch.search(mSearchMetadata.getNextResultsUrl(), new SearchCallback() {
                @Override
                public void onQueryComplete(SearchResponse searchResponse) {
                    mQueryInProgress = false;
                    mUiHandler.obtainMessage(NEXT_LOADED, searchResponse).sendToTarget();
                }
            });
        }
    }

    private static class UiHandler extends Handler {

        private final WeakReference<MainActivityFragment> mFragmentRef;

        public UiHandler(MainActivityFragment fragment) {
            super(Looper.getMainLooper());
            mFragmentRef = new WeakReference<MainActivityFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivityFragment f = mFragmentRef.get();
            if (f != null) {
                SearchResponse response = (SearchResponse) msg.obj;
                switch (msg.what) {
                    case QUERY_COMPLETE:
                        f.mSearchMetadata = response.getSearchMetadata();
                        f.mTweetsAdapter.update(response.getTweetList());
                        f.mListView.setOnScrollListener(f);
                        break;
                    case NEXT_LOADED:
                        f.mSearchMetadata = response.getSearchMetadata();
                        f.mTweetsAdapter.next(response.getTweetList());
                        if (!f.mSearchMetadata.hasNextResults()) {
                            f.mFooterView.setVisibility(View.GONE);
                            f.mListView.setOnScrollListener(null);
                        }
                        break;
                    case REFRESH:
                        f.mSearchMetadata = response.getSearchMetadata();
                        f.mTweetsAdapter.newest(response.getTweetList());
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        }
    }

    private static final class TweetsAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        private List<Tweet> mTweetList;

        public TweetsAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        public void update(List<Tweet> tweets) {
            mTweetList = new ArrayList<Tweet>(tweets);
            notifyDataSetChanged();
        }

        public void next(List<Tweet> tweetList) {
            mTweetList.addAll(tweetList);
            notifyDataSetChanged();
        }

        public void newest(List<Tweet> tweetList) {
            mTweetList.addAll(0, tweetList);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mTweetList == null ? 0 : mTweetList.size();
        }

        @Override
        public Tweet getItem(int position) {
            return mTweetList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mTweetList.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.tweet_adapter_layout,
                        parent, false);
            }
            TextView tv = ViewHolder.get(convertView, R.id.text);

            Tweet tweet = getItem(position);
            tv.setText(tweet.getText());
            return convertView;
        }

        private static final class ViewHolder {

            @SuppressWarnings("unchecked")
            public static <T extends View> T get(View view, int id) {
                SparseArray<View> holder = (SparseArray<View>) view.getTag();
                if (holder == null) {
                    holder = new SparseArray<View>();
                    view.setTag(holder);
                }

                View child = holder.get(id);
                if (child == null) {
                    child = view.findViewById(id);
                    holder.put(id, child);
                }
                return (T) child;
            }
        }
    }
}

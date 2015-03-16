package me.gerbit.twitter.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.SearchableInfo;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.Image;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import me.gerbit.twitter.api.SearchCallback;
import me.gerbit.twitter.api.SearchQuery;
import me.gerbit.twitter.api.OkSearchResponse;
import me.gerbit.twitter.api.SearchResponse;
import me.gerbit.twitter.api.TwitterSearch;
import me.gerbit.twitter.data.SearchMetadata;
import me.gerbit.twitter.data.Tweet;

public class MainActivityFragment extends Fragment implements AbsListView.OnScrollListener {

    private static final String TAG = MainActivityFragment.class.getSimpleName();

    private static final int QUERY_COMPLETE = 0;
    private static final int NEXT_LOADED = 1;
    private static final int REFRESH = 2;
    private static final int QUERY_ERROR = 3;

    private ListView mListView;

    private UiHandler mUiHandler;

    private TweetsAdapter mTweetsAdapter;

    private SearchMetadata mSearchMetadata;

    private TwitterSearch mTwitterSearch;

    private View mFooterView;

    private boolean mQueryInProgress;

    private Menu mMenu;

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

        mMenu = menu;

        inflater.inflate(R.menu.menu_main, menu);

        final SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();

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

                    @Override
                    public void onError(int code, String msg) {
                        mUiHandler.obtainMessage(QUERY_ERROR, code, 0, msg).sendToTarget();
                    }

                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        search.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchView view = (SearchView) v;
                view.setQuery(mSearchMetadata != null ? Uri.decode(mSearchMetadata.getQuery()) : "", false);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                if (mQueryInProgress || mSearchMetadata == null) {
                    return true;
                }
                // TODO Custom animation (default ProgressBar?)
                ((AnimationDrawable) item.getIcon()).start();
                mQueryInProgress = true;
                mTwitterSearch.search(mSearchMetadata.getRefreshUrl(), new SearchCallback() {
                    @Override
                    public void onQueryComplete(SearchResponse searchResponse) {
                        mQueryInProgress = false;
                        mUiHandler.obtainMessage(REFRESH, searchResponse).sendToTarget();
                    }

                    @Override
                    public void onError(int code, String msg) {
                        mUiHandler.obtainMessage(QUERY_ERROR, code, 0, msg).sendToTarget();
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

                @Override
                public void onError(int code, String msg) {
                    mUiHandler.obtainMessage(QUERY_ERROR, code, 0, msg).sendToTarget();
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
                SearchResponse response = null;
                switch (msg.what) {
                    case QUERY_COMPLETE:
                        response = (SearchResponse) msg.obj;
                        f.mSearchMetadata = response.getSearchMetadata();
                        f.mTweetsAdapter.update(response.getTweetList());
                        f.mListView.setOnScrollListener(f);
                        break;
                    case NEXT_LOADED:
                        response = (SearchResponse) msg.obj;
                        f.mSearchMetadata = response.getSearchMetadata();
                        f.mTweetsAdapter.next(response.getTweetList());
                        if (!f.mSearchMetadata.hasNextResults()) {
                            f.mFooterView.setVisibility(View.GONE);
                            f.mListView.setOnScrollListener(null);
                        }
                        break;
                    case REFRESH:
                        response = (SearchResponse) msg.obj;
                        f.mSearchMetadata = response.getSearchMetadata();
                        f.mTweetsAdapter.newest(response.getTweetList());
                        f.mListView.setSelectionAfterHeaderView();
                        ((AnimationDrawable)(f.mMenu.findItem(R.id.action_refresh).getIcon())).stop();
                        break;
                    case QUERY_ERROR:
                        if (f.getActivity() != null) {
                            Toast.makeText(f.getActivity(), (String) msg.obj, Toast.LENGTH_LONG).show();
                            ((AnimationDrawable)(f.mMenu.findItem(R.id.action_refresh).getIcon())).stop();
                        }
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
            TextView text = ViewHolder.get(convertView, R.id.text);
            TextView name = ViewHolder.get(convertView, R.id.name);
            TextView screenName = ViewHolder.get(convertView, R.id.screen_name);
            ImageView profileImg = ViewHolder.get(convertView, R.id.profile_img);

            Tweet tweet = getItem(position);
            text.setText(tweet.getText());
            name.setText(tweet.getUser().getName());
            screenName.setText("@" + tweet.getUser().getScreenName());

            return convertView;
        }

        private static final class ViewHolder {

            @SuppressWarnings("unchecked")
            public static <T extends View> T get(final View view, final int id) {
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

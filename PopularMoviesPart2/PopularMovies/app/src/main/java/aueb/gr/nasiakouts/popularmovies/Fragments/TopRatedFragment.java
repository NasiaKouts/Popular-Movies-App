package aueb.gr.nasiakouts.popularmovies.Fragments;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import aueb.gr.nasiakouts.popularmovies.R;

public class TopRatedFragment extends MoviesFragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        moviesRecyclerViewAdapter.setSortedBy(getString(R.string.sort_by_rating));

        startLoader();

        return root;
    }

    @Override
    public void startLoader(){
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.shared_pref_sort_key), getString(R.string.sort_by_rating));

        if(getActivity() != null) {
            LoaderManager loaderManager = getActivity().getSupportLoaderManager();
            Loader<String> apiFetchLoader = loaderManager.getLoader(MOVIE_FETCH_USING_API);
            if (apiFetchLoader == null) {
                loaderManager.initLoader(MOVIE_FETCH_USING_API, bundle, MovieApiLoader);
            } else {
                loaderManager.restartLoader(MOVIE_FETCH_USING_API, bundle, MovieApiLoader);
            }
        }
    }
}

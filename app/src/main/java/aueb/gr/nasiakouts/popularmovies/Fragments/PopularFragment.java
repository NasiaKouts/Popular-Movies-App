package aueb.gr.nasiakouts.popularmovies.Fragments;

import android.os.Bundle;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import aueb.gr.nasiakouts.popularmovies.R;

public class PopularFragment extends MoviesFragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        moviesRecyclerViewAdapter.setSortedBy(getString(R.string.sort_by_popularity));

        startLoader();
        return view;
    }

    @Override
    public void startLoader(){
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.shared_pref_sort_key), getString(R.string.sort_by_popularity));

        if(getActivity() != null){
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

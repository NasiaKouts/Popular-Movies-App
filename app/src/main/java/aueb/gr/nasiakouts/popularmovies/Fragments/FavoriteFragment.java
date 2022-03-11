package aueb.gr.nasiakouts.popularmovies.Fragments;

import android.os.Bundle;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import aueb.gr.nasiakouts.popularmovies.R;

public class FavoriteFragment extends MoviesFragment {
    public static boolean foundChanges;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        moviesRecyclerViewAdapter.setSortedBy(getString(R.string.fav));
        startLoader();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        /*
           A movie has been removed from favorites
           thus we need to restart the loader
         */
        if(foundChanges){
            foundChanges = false;
            startLoader();
        }
    }

    @Override
    public void startLoader(){
        if(getActivity() != null) {
            LoaderManager loaderManager = getActivity().getSupportLoaderManager();
            Loader<String> favLoader = loaderManager.getLoader(FAVORITES_FETCH_USING_DB);

            if (favLoader == null) {
                loaderManager.initLoader(FAVORITES_FETCH_USING_DB, null, FavoriteMoviesLoader);
            } else {
                loaderManager.restartLoader(FAVORITES_FETCH_USING_DB, null, FavoriteMoviesLoader);
            }
        }
    }
}

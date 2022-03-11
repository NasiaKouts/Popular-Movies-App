package aueb.gr.nasiakouts.popularmovies.Activities;

import androidx.annotation.NonNull;
import aueb.gr.nasiakouts.popularmovies.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import java.util.List;
import aueb.gr.nasiakouts.popularmovies.Fragments.FavoriteFragment;
import aueb.gr.nasiakouts.popularmovies.Fragments.MoviesFragment;
import aueb.gr.nasiakouts.popularmovies.Fragments.PopularFragment;
import aueb.gr.nasiakouts.popularmovies.Fragments.TopRatedFragment;
import aueb.gr.nasiakouts.popularmovies.R;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Replace ActionBar with the Toolbar
        setSupportActionBar(binding.toolbar.getRoot());
        ActionBar actionbar = getSupportActionBar();
        if(actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        binding.navView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        openSelectedFragment(menuItem);
                        return true;
                    }
                });

        // highlight item
        checkItem(savedInstanceState);

        // set the title of the Action Bar according to the selected item on the drawer
        switch (getCheckedItem()) {
            case 0:
                setTitle(R.string.sort_by_popularity);
                break;
            case 1:
                setTitle(R.string.sort_by_rating);
                break;
            default:
                setTitle(R.string.fav);
        }

        // by default open up PopularFragment
        if(savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            try {
                fragmentManager.beginTransaction()
                        .replace(R.id.selected_fragment, PopularFragment.class.newInstance())
                        .commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // Open / close the drawer when the Home - Up navigation button is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                binding.drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(getString(R.string.shared_pref_sort_key), getCheckedItem());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        checkItem(savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }

    private int getCheckedItem() {
        Menu menu = binding.navView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.isChecked()) {
                return i;
            }
        }
        return -1;
    }

    // Highlight the corresponding item
    private void checkItem(Bundle savedInstanceState){
        if(savedInstanceState != null) {
            int indexOfCheckedItem = savedInstanceState.getInt(getString(R.string.shared_pref_sort_key), -1);
            if(indexOfCheckedItem != -1) {
                switch (indexOfCheckedItem) {
                    case 0: binding.navView.setCheckedItem(R.id.nav_popular);
                        break;
                    case 1: binding.navView.setCheckedItem(R.id.nav_top_rated);
                        break;
                    default: binding.navView.setCheckedItem(R.id.nav_favorites);
                        break;
                }
            }
        }
        else {
            // default checked item to check is the Popular one;
            binding.navView.setCheckedItem(R.id.nav_popular);
        }
    }

    private void openSelectedFragment(MenuItem menuItem) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if(fragments != null && fragments.size() > 0){
            fragmentManager.beginTransaction().remove(fragments.get(0)).commit();
        }

        Fragment selectedFragment = null;
        Class selectedFragmentClass;

        switch(menuItem.getItemId()) {
            case R.id.nav_popular:
                selectedFragmentClass = PopularFragment.class;
                getSupportLoaderManager().destroyLoader(MoviesFragment.FAVORITES_FETCH_USING_DB);
                break;
            case R.id.nav_top_rated:
                selectedFragmentClass = TopRatedFragment.class;
                getSupportLoaderManager().destroyLoader(MoviesFragment.FAVORITES_FETCH_USING_DB);
                break;
            default:
                selectedFragmentClass = FavoriteFragment.class;
                getSupportLoaderManager().destroyLoader(MoviesFragment.MOVIE_FETCH_USING_API);
                break;
        }

        try {
            selectedFragment = (Fragment) selectedFragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }


        fragmentManager.beginTransaction().replace(R.id.selected_fragment, selectedFragment).commit();

        // highlight the selected item
        menuItem.setChecked(true);

        // Set Action Bar title
        setTitle(menuItem.getTitle());

        // Close the navigation drawer
        binding.drawerLayout.closeDrawers();
    }
}

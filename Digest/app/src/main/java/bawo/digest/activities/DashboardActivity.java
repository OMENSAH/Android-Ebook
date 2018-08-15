package bawo.digest.activities;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import bawo.digest.R;
import bawo.digest.adapters.ArticleAdapter;
import bawo.digest.data.MockArticleData;
import bawo.digest.fragments.NavigationDrawerFragment;

public class DashboardActivity extends AppCompatActivity {
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        initializeViews();
        setupDrawer();
        setupMenu();
        setupRecyclerView();
    }

    private void initializeViews(){
        toolbar = findViewById(R.id.dashboard_toolbar);
    }

    private void setupDrawer() {
        NavigationDrawerFragment fragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.dashboard_drawerNavigationFragment);
        DrawerLayout drawerLayout = findViewById(R.id.dashboard_drawerLayout);
        fragment.setUpDrawer(R.id.dashboard_drawerNavigationFragment, drawerLayout, toolbar);
    }

    private void setupMenu() {
        toolbar.inflateMenu(R.menu.menus);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = null;
                switch (item.getItemId()) {
                    case R.id.add:
                        intent = new Intent(DashboardActivity.this, PostActivity.class);
                        break;

                    case R.id.review:

                        break;
                }
                startActivity(intent);
                return false;
            }
        });
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.dashboard_recyclerView);
        ArticleAdapter adapter = new ArticleAdapter(this, MockArticleData.getData());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}

package com.example.whatszappy.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.whatszappy.R;
import com.example.whatszappy.config.ConfigFirebase;
import com.example.whatszappy.fragment.ContatosFragment;
import com.example.whatszappy.fragment.ConversasFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private MaterialSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("WhatsZappy");
        setSupportActionBar(toolbar);

        auth = ConfigFirebase.getFirebaseAuth();

        //Configurar abas
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
            getSupportFragmentManager(),
            FragmentPagerItems.with(this)
            .add("Conversas", ConversasFragment.class)
            .add("Contatos", ContatosFragment.class)
            .create()
        );

        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter( adapter );

        SmartTabLayout viewPagerTab = findViewById(R.id.viewPagerTab);
        viewPagerTab.setViewPager( viewPager );

        //biblioteca para pesquisa
        searchView = findViewById(R.id.search_view);

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                ConversasFragment fragment = (ConversasFragment) adapter.getPage( 0);
                fragment.chargeC();

            }
        });
        //
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Log.d("evento", "onQueryTextSubmit");
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                //Log.d("evento", "onQueryTextChange");
                ConversasFragment fragment = (ConversasFragment) adapter.getPage( 0);

                if ( newText != null && !newText.isEmpty() ){
                    fragment.searchConversas( newText.toLowerCase() );
                }

                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        //botao pesquisa
        MenuItem item = menu.findItem(R.id.menuPesquisa);
        searchView.setMenuItem(item);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            //case R.id.:

                //break;
            case R.id.menuConfig:
                startActivity(new Intent(MainActivity.this, ConfigActivity.class));
                break;
            case R.id.menuLogoff:
                userLogoff();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void userLogoff(){

        try {
            auth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }catch (Exception e){
            e.printStackTrace();
        }
    }




}

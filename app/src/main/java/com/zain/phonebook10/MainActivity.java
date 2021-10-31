package com.zain.phonebook10;

import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.zain.phonebook10.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
private ActivityMainBinding binding;
    private int countBackPressed;
    public static SqlLiteHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //current_fragment = "Phonebook";
        countBackPressed = 0;

         binding = ActivityMainBinding.inflate(getLayoutInflater());
         setContentView(binding.getRoot());
         db = new SqlLiteHelper(this.getApplicationContext());
         //db.clear();
         setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);



    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        countBackPressed++;
        if (countBackPressed>2)
        {
            System.exit(0);
        }
        /*Fragment curFrag = this.getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main).getTargetFragment();
        curFrag.getChildFragmentManager().getFragments().get(current_fragment);
        if (current_fragment.equals("Contacts"))*/

    }


}
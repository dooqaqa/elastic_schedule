package com.dqq.elasticschedule;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
/**
 * Created by ZhengJiefu on 2015/7/9.
 */
public class ContentFragment extends Fragment {
    private ListView mTargetsListView = null;
    public ContentFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mTargetsListView = (ListView) inflater.inflate(
                R.layout.fragment_content_main, container, false);
        mTargetsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
                //item_list[item_list.length] = "新建";
                java.util.ArrayList item_list = ScheduleManager.GetInstance().OpenSchedule(ScheduleManager.GetInstance().GetCurrentScheduleIndex());
        item_list.add("插入");
        mTargetsListView.setAdapter(new ArrayAdapter<String>(
                getActionBar().getThemedContext(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                item_list
        ));
        //mTargetsListView.setItemChecked(0, true);
        return mTargetsListView;
    }

    private void selectItem(int position) {
        if (mTargetsListView != null) {
            mTargetsListView.setItemChecked(position, true);
            if (position + 1 == mTargetsListView.getCount()) {
                // 新建
            }
            java.util.ArrayList target_list = ScheduleManager.GetInstance().OpenSchedule(position);
        }
//        if (mCallbacks != null) {
//            mCallbacks.onNavigationDrawerItemSelected(position);
//        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            //mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
       // mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_example) {
            Toast.makeText(getActivity(), ScheduleManager.GetInstance().GetScheduleName(ScheduleManager.GetInstance().GetCurrentScheduleIndex()), Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(ScheduleManager.GetInstance().GetScheduleName(ScheduleManager.GetInstance().GetCurrentScheduleIndex()));
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }
}

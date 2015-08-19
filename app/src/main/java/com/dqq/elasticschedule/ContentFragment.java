package com.dqq.elasticschedule;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.dqq.elasticschedule.DragListView;

/**
 * Created by dooqaqa on 2015/7/9.
 */
public class ContentFragment extends Fragment implements ScheduleObserver, DragListView.DragListViewListener {
    private DragListView mTargetsListView = null;
    public ContentFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScheduleManager.GetInstance().RegObserver(this, true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        RefreshList();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mTargetsListView = (DragListView) inflater.inflate(
                R.layout.fragment_content_main, container, false);
        mTargetsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
        mTargetsListView.setAdapter(new CustomAdapter(
                getActionBar().getThemedContext(),
                R.layout.drag_list_item_tag,
                R.id.drag_list_item_text,
                new ArrayList<String>()
        ));
        mTargetsListView.AddListener(this);
        return mTargetsListView;
    }

    private void selectItem(int position) {
        if (mTargetsListView != null) {
            mTargetsListView.setItemChecked(position, false);
            if (position + 1 == mTargetsListView.getCount()) {
                final EditText inputServer = new EditText(this.getActivity());
                AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
                builder.setTitle(getActivity().getString(R.string.add_milestone))./*setIcon(android.R.drawable.ic_dialog_info).*/setView(inputServer)
                        .setNegativeButton(getActivity().getString(R.string.messagebox_cancle), null);
                builder.setPositiveButton(getActivity().getString(R.string.messagebox_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String target = inputServer.getText().toString();
                        if (!target.isEmpty()) {
                            ScheduleManager.Schedule m = ScheduleManager.GetInstance().new Schedule();
                            m.name = target;
                            m.established_time = Calendar.getInstance();
                            m.established_time.setTime(new Date(System.currentTimeMillis()));
                            ScheduleManager.GetInstance().AddMileStone(m, ScheduleManager.GetInstance().GetCurrentSchedule().MilestoneCount());
                            RefreshList();
                        }
                    }
                });
                builder.show();
            }
        }
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
        ScheduleManager.GetInstance().RegObserver(this, false);
       // mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            final EditText inputServer = new EditText(this.getActivity());
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
            builder.setTitle(getActivity().getString(R.string.delete_schedule_prompt))./*setIcon(android.R.drawable.ic_dialog_info).*/setView(inputServer)
                    .setNegativeButton(R.string.messagebox_cancle, null);
            builder.setPositiveButton(R.string.messagebox_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    String target = inputServer.getText().toString();
                    if (0 == target.compareTo(getActivity().getString(R.string.delete_schedule_answer))) {
                        ScheduleManager.GetInstance().DeleteSchedule(ScheduleManager.GetInstance().GetCurrentScheduleIndex());
                    } else {
                        Toast.makeText(getActivity(), getActivity().getString(R.string.delete_schedule_badinput), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.show();
            Log.e("onOptionsItemSelected", "action_delete");
            return true;
        } else if (id == R.id.action_example) {
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
    @Override
    public void  NotifyScheduleOpened(long id){RefreshList();}
    @Override
    public void  NotifyScheduleListChanged(){}
    @Override
    public void NotifyScheduleDeleted(long index){
        mTargetsListView.setAdapter(new CustomAdapter(
                getActionBar().getThemedContext(),
                R.layout.drag_list_item_tag,
                R.id.drag_list_item_text,
                new ArrayList<String>()
        ));
    }
    private void RefreshList(){
        ScheduleManager.Schedule s = ScheduleManager.GetInstance().GetSchedule(ScheduleManager.GetInstance().GetCurrentScheduleIndex());
        ArrayList<String> item_list = new ArrayList<String>();
        if (null != s.milestones) {
            for (ScheduleManager.Schedule ss : s.milestones) {
                item_list.add(ss.name);
            }
        }
        if (null != s.name) item_list.add(s.name);
        item_list.add(getActivity().getString(R.string.add_milestone));
        mTargetsListView.setAdapter(new CustomAdapter(
                getActionBar().getThemedContext(),
                R.layout.drag_list_item_tag,
                R.id.drag_list_item_text,
                item_list
        ));
    }
    public void OnDragFinish(int sourcepos, int targetpos) {
        ScheduleManager.GetInstance().MoveMilesone(sourcepos, targetpos);
    }
    public boolean IsPositionDragable(int position) {
        return position >= 0 && position < mTargetsListView.getCount() - 2;
    }
}

package net.kazhik.gambarumeter.history;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.kazhik.gambarumeter.R;
import net.kazhik.gambarumeter.entity.WorkoutInfo;
import net.kazhik.gambarumeter.pager.PagerFragment;
import net.kazhik.gambarumeter.storage.HeartRateTable;
import net.kazhik.gambarumeter.storage.LocationTable;
import net.kazhik.gambarumeter.storage.SplitTable;
import net.kazhik.gambarumeter.storage.StepCountTable;
import net.kazhik.gambarumeter.storage.WorkoutTable;

import java.util.List;

/**
 * Created by kazhik on 14/11/11.
 */
public abstract class HistoryFragment extends PagerFragment
        implements WearableListView.ClickListener,
        DialogInterface.OnClickListener {

    private long startTime;
    private boolean editMode = false;
    private static final String TAG = "HistoryFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.workout_history, container, false);
    }

    public long getStartTime() {
        return this.startTime;
    }

    @Override
    public void refreshView() {
        WorkoutTable workoutTable = new WorkoutTable(this.getActivity());
        workoutTable.open(true);
        List<WorkoutInfo> workoutInfos = workoutTable.selectAll();
        workoutTable.close();

        HistoryAdapter adapter = new HistoryAdapter(this.getActivity(), workoutInfos);
        WearableListView listView = (WearableListView)this.getActivity().findViewById(R.id.history_list);
        if (listView == null) {
            Log.d(TAG, "historyList not found");
            return;
        }
        listView.setAdapter(adapter);
        listView.setClickListener(this);
        listView.setGreedyTouchMode(true);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onStart() {
        super.onStart();

        this.refreshView();

    }

    public abstract void openDetailFragment();

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        this.startTime = (Long)viewHolder.itemView.getTag();

        if (this.editMode) {
            AlertDialog confirmDelete =
                    new AlertDialog.Builder(this.getActivity())
                            .setMessage(R.string.confirm_delete)
                            .setPositiveButton(R.string.delete, this)
                            .setNegativeButton(R.string.cancel, this)
                            .create();

            confirmDelete.show();
        } else {
            this.openDetailFragment();
        }

    }

    @Override
    public void onTopEmptyRegionClick() {
        this.editMode = !this.editMode;

        TextView modeText = (TextView)this.getActivity().findViewById(R.id.mode);
        int msgId = this.editMode? R.string.edit_mode: R.string.view_mode;
        modeText.setText(msgId);
    }
    
    protected void deleteRecord(Context context, long startTime) {
        Log.d(TAG, "deleteRecord: " + startTime);

        WorkoutTable workoutTable = new WorkoutTable(context);
        workoutTable.open(false);
        workoutTable.delete(startTime);
        workoutTable.close();

        StepCountTable stepCountTable = new StepCountTable(context);
        stepCountTable.open(false);
        stepCountTable.delete(startTime);
        stepCountTable.close();

        SplitTable splitTable = new SplitTable(context);
        splitTable.open(false);
        splitTable.delete(startTime);
        splitTable.close();
        
    }
    
    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            Log.d(TAG, "Delete: " + this.startTime);

            Context context = this.getActivity();
            
            this.deleteRecord(context, this.startTime);

            this.refreshView();
        } else if (which == DialogInterface.BUTTON_NEGATIVE) {
            Log.d(TAG, "Cancel");
        }

    }

}

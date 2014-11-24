package net.kazhik.gambarumeter.history;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.kazhik.gambarumeter.R;
import net.kazhik.gambarumeter.entity.WorkoutInfo;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by kazhik on 14/11/10.
 */
public class HistoryAdapter extends WearableListView.Adapter {
    private List<WorkoutInfo> dataSet;
    private final Context context;
    private final LayoutInflater inflater;
    private static final String TAG = "HistoryAdapter";

    // Provide a suitable constructor (depends on the kind of dataset)
    public HistoryAdapter(Context context, List<WorkoutInfo> dataset) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.dataSet = dataset;
    }

    // Provide a reference to the type of views you're using
    public static class ItemViewHolder extends WearableListView.ViewHolder {
        private TextView startTimeText;
        private TextView splitTimeText;
        public ItemViewHolder(View itemView) {
            super(itemView);
            // find the text view within the custom item's layout
            this.startTimeText = (TextView) itemView.findViewById(R.id.start_time);
            this.splitTimeText = (TextView) itemView.findViewById(R.id.split_time);
        }
    }

    // Create new views for list items
    // (invoked by the WearableListView's layout manager)
    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {

        // Inflate our custom layout for list items
        return new ItemViewHolder(this.inflater.inflate(R.layout.workout_history_item, null));
    }

    // Replace the contents of a list item
    // Instead of creating new views, the list tries to recycle existing ones
    // (invoked by the WearableListView's layout manager)
    @Override
    public void onBindViewHolder(WearableListView.ViewHolder holder,
                                 int position) {
        // retrieve the text view
        ItemViewHolder itemHolder = (ItemViewHolder) holder;
        TextView startTimeText = itemHolder.startTimeText;
        TextView splitTimeText = itemHolder.splitTimeText;

        // replace text contents
        WorkoutInfo workout = this.dataSet.get(position);
        long startTime = workout.getStartTime();
        long stopTime = workout.getStopTime();
        String startTimeStr = DateFormat.getDateTimeInstance().format(new Date(startTime));
        startTimeText.setText(startTimeStr);
        Log.d(TAG, startTime + ":" + stopTime);
        String splitTimeStr = this.formatSplitTime(stopTime - startTime);
        splitTimeText.setText(splitTimeStr);
        // replace list item's metadata
        holder.itemView.setTag(startTime);
    }
    private String formatSplitTime(long splitTime) {
        long splitTimeSec = splitTime / 1000;

        long hour = splitTimeSec / 60 / 60;
        long min = splitTimeSec / 60 - (hour * 60);
        long sec = splitTimeSec % 60;

        return String.format("%02d:%02d:%02d", hour, min, sec);
    }

    // Return the size of your dataset
    // (invoked by the WearableListView's layout manager)
    @Override
    public int getItemCount() {
        return this.dataSet.size();
    }


}

package com.example.rafa.liquidgalaxypoiscontroller;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RAFA on 18/06/2015.
 */
public class TourPOIsAdapter extends BaseAdapter {

    private static List<String> pois;
    private static List<Integer> poisDuration = new ArrayList<Integer>();
    private FragmentActivity activity;
    private static String type = "creating";
    private static int global_interval = 0;

    public TourPOIsAdapter(FragmentActivity activity, List<String> tourPOIsNames) {
        this.activity = activity;
        this.pois = tourPOIsNames;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return pois.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return pois.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link android.view.LayoutInflater#inflate(int, android.view.ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;
        String poi = (String) getItem(position);

        if(convertView == null){
            LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inf.inflate(R.layout.tour_pois_list_item, parent, false);
        }

        TextView name = (TextView) view.findViewById(R.id.poi_complete_name);
        name.setText(poi);

        if(position == 0){
            view.findViewById(R.id.move_up).setVisibility(View.INVISIBLE);
        }
        if(position == getCount() - 1){
            view.findViewById(R.id.move_down).setVisibility(View.INVISIBLE);
        }

        view.findViewById(R.id.move_down).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toMoveDown = (String) getItem(position);
                String toMoveUp = (String) getItem(position + 1);
                pois.remove(position + 1);
                pois.remove(position);
                pois.add(position, toMoveUp);
                pois.add(position + 1, toMoveDown);
                notifyDataSetChanged();
            }
        });

        view.findViewById(R.id.move_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toMoveUp = (String) getItem(position);
                String toMoveDown = (String) getItem(position - 1);
                pois.remove(position);
                pois.remove(position - 1);
                pois.add(position - 1, toMoveUp);
                pois.add(position, toMoveDown);

                notifyDataSetChanged();
            }
        });

        if(type.equals("updating")) {
            EditText seconds = (EditText) view.findViewById(R.id.poi_seconds);
            int poi_interval = poisDuration.get(position);
            if(global_interval == poi_interval){
                seconds.setText("");
            }else {
                seconds.setText(String.valueOf(poi_interval));
            }
        }

        return view;
    }

    public static void setPOIsDuration(List<Integer> durationList) {
        poisDuration.clear();
        poisDuration.addAll(durationList);
    }

    public static void setType(String t){
        type = t;
    }

    public static void setGlobalInterval(int globalInterval) {
        global_interval = globalInterval;
    }
}
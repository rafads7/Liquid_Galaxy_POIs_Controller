package com.example.rafa.liquidgalaxypoiscontroller;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.rafa.liquidgalaxypoiscontroller.data.POIsContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*This class has the same objective that CreateItemFragment once but it is called when one user
wants to update one item already created. The pages are structurally equals with the fragment mentioned before,
however there is a differences: all fields are filled by the values of the item to update.
*/
public class UpdateItemFragment extends android.support.v4.app.Fragment {

    private static View rootView;
    private String updateType, newShownName;
    private static String itemSelectedID;
    private Cursor queryCursor;
    private static ViewHolderTour viewHolderTour;
    private ArrayAdapter<String> adapter;
    private static Map<String,String> spinnerIDsAndShownNames, categoriesOfPOIsSpinner;
    private static List<String> tourPOIsNames, tourPOIsIDs, tourExistingPOIsNames, tourExistingPOIsIDs;
    private static HashMap<String, String> namesAndIDs = new HashMap<String, String>();
    private static final String POI_IDselection = POIsContract.POIEntry._ID + " =?";
    private static final String TOUR_IDselection = POIsContract.TourEntry._ID + " =?";
    private static final String Category_IDselection = POIsContract.CategoryEntry._ID + " =?";

    public UpdateItemFragment() {
        tourPOIsIDs = new ArrayList<String>();
        tourPOIsNames = new ArrayList<String>();
        tourExistingPOIsNames = new ArrayList<String>();
        tourExistingPOIsIDs = new ArrayList<String>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        Bundle extras = getActivity().getIntent().getExtras();
        rootView = null;

        if(extras!=null){
            this.updateType = extras.getString("UPDATE_TYPE");
            this.itemSelectedID = extras.getString("ITEM_ID");
        }

        if(updateType.equals("POI")){

            ViewHolderPoi viewHolder = setPOILayoutSettings(inflater, container);
            updatePOI(viewHolder);
        }
        else if(updateType.equals("TOUR")){

            viewHolderTour = setTOURLayoutSettings(inflater, container);
            updateTOUR(viewHolderTour);
        }
        else {//CATEGORY
            ViewHolderCategory viewHolder = setCategoryLayoutSettings(inflater, container);
            updateCategory(viewHolder);
        }

        return rootView;
    }

    public static class ViewHolderPoi {

        public int ID = 0;
        public int NAME = 1;
        public int VISITED_PLACE_NAME = 2;
        public int LONGITUDE = 3;
        public int LATITUDE = 4;
        public int ALTITUDE = 5;
        public int HEADING = 6;
        public int TILT = 7;
        public int RANGE = 8;
        public int ALTITUDE_MODE = 9;
        public int HIDE = 10;
        public int CATEGORY_ID = 11;

        public EditText nameET;
        public EditText visitedPlaceET;
        public EditText longitudeET;
        public EditText latitudeET;
        public EditText altitudeET;
        public EditText headingET;
        public EditText tiltET;
        public EditText rangeET;
        public EditText altitudeModeET;
        public EditText hide;
        public Spinner categoryID;
        public FloatingActionButton createPOI;
        public FloatingActionButton updatePOI;
        public FloatingActionButton cancel;

        public ViewHolderPoi(View rootView) {

            nameET = (EditText) rootView.findViewById(R.id.name);
            visitedPlaceET = (EditText) rootView.findViewById(R.id.visited_place);
            longitudeET = (EditText) rootView.findViewById(R.id.longitude);
            latitudeET = (EditText) rootView.findViewById(R.id.latitude);
            altitudeET = (EditText) rootView.findViewById(R.id.altitude);
            headingET = (EditText) rootView.findViewById(R.id.heading);
            tiltET = (EditText) rootView.findViewById(R.id.tilt);
            rangeET = (EditText) rootView.findViewById(R.id.range);
            altitudeModeET = (EditText) rootView.findViewById(R.id.altitudeMode);
            hide = (EditText) rootView.findViewById(R.id.poi_hide);
            categoryID = (Spinner) rootView.findViewById(R.id.categoryID_spinner);
            createPOI = (FloatingActionButton) rootView.findViewById(R.id.create_poi);
            updatePOI = (FloatingActionButton) rootView.findViewById(R.id.update_poi);
            cancel = (FloatingActionButton) rootView.findViewById(R.id.cancel_come_back);
        }
    }
    public static class ViewHolderTour {

        public int NAME = 1;
        public int CATEGORY = 2;
        public int HIDE = 3;
        public int INTERVAL = 4;

        public EditText tourName;
        public EditText hide;
        public Spinner categoryID;
        public android.support.design.widget.FloatingActionButton createTOUR;
        public android.support.design.widget.FloatingActionButton updateTOUR;
        public ListView addedPois;
        public ImageView up;
        public ImageView down;
        public FloatingActionButton cancel;
        public EditText global_interval;

        public ViewHolderTour(View rootView) {

            tourName = (EditText) rootView.findViewById(R.id.tour_name);
            hide = (EditText) rootView.findViewById(R.id.tour_hide);
            categoryID = (Spinner) rootView.findViewById(R.id.categoryID_spinner);
            createTOUR = (android.support.design.widget.FloatingActionButton) rootView.findViewById(R.id.create_tour);
            updateTOUR = (android.support.design.widget.FloatingActionButton) rootView.findViewById(R.id.update_tour);
            addedPois = (ListView) rootView.findViewById(R.id.tour_pois_listview);
            up = (ImageView) rootView.findViewById(R.id.move_up);
            down = (ImageView) rootView.findViewById(R.id.move_down);
            cancel = (FloatingActionButton) rootView.findViewById(R.id.cancel_come_back);
            global_interval = (EditText) rootView.findViewById(R.id.pois_interval);
        }
    }
    public static class ViewHolderCategory {

        private int ID = 0;
        private int NAME = 1;
        private int FATHER_ID = 2;
        private int SHOWN_NAME = 3;
        private int HIDE = 4;

        public EditText categoryName;
        public EditText hide;
        public Spinner fatherID;
        public FloatingActionButton createCategory;
        public FloatingActionButton updateCategory;
        public FloatingActionButton cancel;

        public ViewHolderCategory(View rootView) {

            categoryName = (EditText) rootView.findViewById(R.id.category_name);
            hide = (EditText) rootView.findViewById(R.id.category_hide);
            fatherID = (Spinner) rootView.findViewById(R.id.father_spinner);
            createCategory = (FloatingActionButton) rootView.findViewById(R.id.create_category);
            updateCategory = (FloatingActionButton) rootView.findViewById(R.id.update_category);
            cancel = (FloatingActionButton) rootView.findViewById(R.id.cancel_come_back);

        }
    }

    /*POI TREATMENT*/
    private void updatePOI(ViewHolderPoi viewHolder) {
        Cursor query = getAllSelectedItemData(POIsContract.POIEntry.CONTENT_URI, POI_IDselection);
        fillPOIsCategoriesSpinner(viewHolder.categoryID);
        setDataToPOIsLayout(query, viewHolder);
        updatePOIModifications(viewHolder);
    }
    private ViewHolderPoi setPOILayoutSettings(LayoutInflater inflater, ViewGroup container) {
        rootView = inflater.inflate(R.layout.fragment_create_or_update_poi, container, false);
        final ViewHolderPoi viewHolder = new ViewHolderPoi(rootView);
        viewHolder.createPOI.setVisibility(View.GONE);
        viewHolder.updatePOI.setVisibility(View.VISIBLE);
        setCancelComeBackBehaviour(viewHolder.cancel);

        return viewHolder;
    }
    private void fillPOIsCategoriesSpinner(Spinner spinner){
        List<String> list = new ArrayList<String>();
        list.add("NO ROUTE");
        spinnerIDsAndShownNames = new HashMap<String, String>();
        categoriesOfPOIsSpinner = new HashMap<String, String>();

        queryCursor = POIsContract.CategoryEntry.getIDsAndShownNamesOfAllCategories(getActivity());

        while(queryCursor.moveToNext()){
            categoriesOfPOIsSpinner.put(String.valueOf(queryCursor.getInt(0)), queryCursor.getString(1));
            spinnerIDsAndShownNames.put(queryCursor.getString(1), String.valueOf(queryCursor.getInt(0)));
            list.add(queryCursor.getString(1));
        }

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
    private void setDataToPOIsLayout(Cursor query, ViewHolderPoi viewHolder){
        if(query.moveToFirst()){
            viewHolder.nameET.setText(query.getString(viewHolder.NAME));
            viewHolder.visitedPlaceET.setText(query.getString(viewHolder.VISITED_PLACE_NAME));
            viewHolder.longitudeET.setText(String.valueOf(query.getFloat(viewHolder.LONGITUDE)));
            viewHolder.latitudeET.setText(String.valueOf(query.getFloat(viewHolder.LATITUDE)));
            viewHolder.altitudeET.setText(String.valueOf(query.getFloat(viewHolder.ALTITUDE)));
            viewHolder.headingET.setText(String.valueOf(query.getFloat(viewHolder.HEADING)));
            viewHolder.tiltET.setText(String.valueOf(query.getFloat(viewHolder.TILT)));
            viewHolder.rangeET.setText(String.valueOf(query.getFloat(viewHolder.RANGE)));
            viewHolder.altitudeModeET.setText(query.getString(viewHolder.ALTITUDE_MODE));
            viewHolder.categoryID.setSelection(adapter.getPosition(getShownNameByCategoryID(query, viewHolder, null, "POI")));
            if(query.getString(viewHolder.HIDE).equals("0")) {
                viewHolder.hide.setText("N");
            }else{
                viewHolder.hide.setText("Y");
            }
        }
    }
    private void updatePOIModifications(final ViewHolderPoi viewHolder){

        viewHolder.updatePOI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContentValues contentValues = new ContentValues();

                String completeName = "", visitedPlace = "", altitudeMode = "";
                float longitude = 0, latitude = 0, altitude = 0, heading = 0, tilt = 0, range = 0;
                int hide = 0, categoryID = 0;

                visitedPlace = viewHolder.visitedPlaceET.getText().toString();
                completeName = viewHolder.nameET.getText().toString();
                longitude = Float.parseFloat(viewHolder.longitudeET.getText().toString());
                latitude = Float.parseFloat(viewHolder.latitudeET.getText().toString());
                altitude = Float.parseFloat(viewHolder.altitudeET.getText().toString());
                heading = Float.parseFloat(viewHolder.headingET.getText().toString());
                tilt = Float.parseFloat(viewHolder.tiltET.getText().toString());
                range = Float.parseFloat(viewHolder.rangeET.getText().toString());
                altitudeMode = viewHolder.altitudeModeET.getText().toString();
                hide = getHideValueFromInputForm(viewHolder.hide);

                String shownName = getShownNameValueFromInputForm(viewHolder.categoryID);
                categoryID = getFatherIDValueFromInputForm(shownName);

                contentValues.put(POIsContract.POIEntry.COLUMN_COMPLETE_NAME, completeName);
                contentValues.put(POIsContract.POIEntry.COLUMN_VISITED_PLACE_NAME, visitedPlace);
                contentValues.put(POIsContract.POIEntry.COLUMN_COMPLETE_NAME, completeName);
                contentValues.put(POIsContract.POIEntry.COLUMN_LONGITUDE, longitude);
                contentValues.put(POIsContract.POIEntry.COLUMN_LATITUDE, latitude);
                contentValues.put(POIsContract.POIEntry.COLUMN_ALTITUDE, altitude);
                contentValues.put(POIsContract.POIEntry.COLUMN_HEADING, heading);
                contentValues.put(POIsContract.POIEntry.COLUMN_TILT, tilt);
                contentValues.put(POIsContract.POIEntry.COLUMN_RANGE, range);
                contentValues.put(POIsContract.POIEntry.COLUMN_ALTITUDE_MODE, altitudeMode);
                contentValues.put(POIsContract.POIEntry.COLUMN_HIDE, hide);
                contentValues.put(POIsContract.POIEntry.COLUMN_CATEGORY_ID, categoryID);

                int updatedRows = POIsContract.POIEntry.updateByID(getActivity(), contentValues, itemSelectedID);
                //int updatedRows = getActivity().getContentResolver().update(POIsContract.POIEntry.CONTENT_URI, contentValues, POI_IDselection, new String[]{itemSelectedID});
                if (updatedRows > 0) {
                    Intent intent = new Intent(getActivity(), LGPCAdminActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "ERROR UPDATING", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), LGPCAdminActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
    private String getShownNameByCategoryID(Cursor query, ViewHolderPoi viewHolderPoi, ViewHolderTour viewHolderTour, String type){
        if(type.equals("POI")) {
            return categoriesOfPOIsSpinner.get(String.valueOf(query.getInt(viewHolderPoi.CATEGORY_ID)));
        }else{
            return categoriesOfPOIsSpinner.get(String.valueOf(query.getInt(viewHolderTour.CATEGORY)));
        }
    }

    /*CATEGORY TREATMENT*/
    private void updateCategory(ViewHolderCategory viewHolder) {
        Cursor query = getAllSelectedItemData(POIsContract.CategoryEntry.CONTENT_URI, Category_IDselection);
        String oldItemShownName = fillCategoriesSpinner(query, viewHolder);
        setDataToLayout(query, viewHolder);
        updateCategoryModifications(viewHolder, oldItemShownName);
    }
    private ViewHolderCategory setCategoryLayoutSettings(LayoutInflater inflater, ViewGroup container) {
        rootView = inflater.inflate(R.layout.fragment_create_or_update_category, container, false);
        final ViewHolderCategory viewHolder = new ViewHolderCategory(rootView);
        viewHolder.createCategory.setVisibility(View.GONE);
        viewHolder.updateCategory.setVisibility(View.VISIBLE);
        setCancelComeBackBehaviour(viewHolder.cancel);

        return viewHolder;
    }
    private String fillCategoriesSpinner(Cursor query, ViewHolderCategory viewHolder){

        query.moveToFirst();
        String itemShownName = query.getString(viewHolder.SHOWN_NAME);

        int id;
        String shownName;
        List<String> list = new ArrayList<String>();
        list.add("NO ROUTE");
        spinnerIDsAndShownNames = new HashMap<String, String>();

        //We get all the categories IDs and ShownNames
        queryCursor = getActivity().getContentResolver().query(POIsContract.CategoryEntry.CONTENT_URI,
                new String[]{POIsContract.CategoryEntry._ID, POIsContract.CategoryEntry.COLUMN_SHOWN_NAME}, null, null, null);

        while(queryCursor.moveToNext()){
            id = queryCursor.getInt(0);
            shownName = queryCursor.getString(1);

            if(!shownName.contains(itemShownName)) {
                spinnerIDsAndShownNames.put(shownName, String.valueOf(id));
                list.add(shownName);
            }
        }

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        viewHolder.fatherID.setAdapter(adapter);

        return itemShownName;
    }
    private void updateCategoryModifications(final UpdateItemFragment.ViewHolderCategory viewHolder, final String oldItemShownName){

        viewHolder.updateCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //If the user wants to put some category inside one of its sons, he will not be able to,
                //he will have to delete that category and insert it again in the correct category.
                //For example: the order is: ESP - CAT - LLEIDA - ... If the user wants
                //to put ESP inside CAT or LLEIDA (both are sons of ESP) he will not be able to.

                ContentValues contentValues = new ContentValues();
                final String categoryName = viewHolder.categoryName.getText().toString();
                final int hideValue = getHideValueFromInputForm(viewHolder.hide);
                String shownNameSelected = getShownNameValueFromInputForm(viewHolder.fatherID);
                final int fatherID = getFatherIDValueFromInputForm(shownNameSelected);
                final String correctShownName = shownNameSelected + viewHolder.categoryName.getText().toString() + "/";
                newShownName = correctShownName;

                contentValues.put(POIsContract.CategoryEntry.COLUMN_NAME, categoryName);
                contentValues.put(POIsContract.CategoryEntry.COLUMN_FATHER_ID, fatherID);
                contentValues.put(POIsContract.CategoryEntry.COLUMN_SHOWN_NAME, correctShownName);
                contentValues.put(POIsContract.CategoryEntry.COLUMN_HIDE, hideValue);

                int updatedRows = POIsContract.CategoryEntry.updateByID(getActivity(), contentValues, itemSelectedID);
                if(updatedRows <= 0) {
                    Toast.makeText(getActivity(), "ERROR UPDATING", Toast.LENGTH_SHORT).show();
                }
                updateSubCategoriesShownName(viewHolder, oldItemShownName);
            }
        });
    }
    private void updateSubCategoriesShownName(ViewHolderCategory viewHolderCategory, String oldItemShownName){

        String whereClause = POIsContract.CategoryEntry.COLUMN_SHOWN_NAME + " LIKE '" + oldItemShownName + "%'";

        Cursor cursor = getActivity().getContentResolver().query(POIsContract.CategoryEntry.CONTENT_URI,
                new String[]{POIsContract.CategoryEntry._ID, POIsContract.CategoryEntry.COLUMN_SHOWN_NAME},
                whereClause, null, null);

        ContentValues updatedShownNameValues;
        String currentShownName, finalShownName, itemTreatedID;
        int updatedRows = 0;
        while(cursor.moveToNext()){

            itemTreatedID = String.valueOf(cursor.getInt(0));
            currentShownName = cursor.getString(1);
            if(!currentShownName.equals(oldItemShownName)) {
                //remove the bad shownName
                String currentWithoutOldPartition = currentShownName.substring(oldItemShownName.length(), currentShownName.length());
                //write the good one
                finalShownName = newShownName + currentWithoutOldPartition;

                updatedShownNameValues = new ContentValues();
                updatedShownNameValues.put(POIsContract.CategoryEntry.COLUMN_SHOWN_NAME, finalShownName);
                updatedRows += getActivity().getContentResolver().update(POIsContract.CategoryEntry.CONTENT_URI, updatedShownNameValues,
                        Category_IDselection, new String[]{itemTreatedID});
            }
        }
        Intent intent = new Intent(getActivity(), LGPCAdminActivity.class);
        startActivity(intent);
    }
    private void setDataToLayout(Cursor query, UpdateItemFragment.ViewHolderCategory viewHolder){
        String fatherShownName = POIsContract.CategoryEntry.getShownNameByID(getActivity(), query.getInt(viewHolder.FATHER_ID));
        if(query.moveToFirst()) {
            viewHolder.categoryName.setText(query.getString(viewHolder.NAME));
            viewHolder.fatherID.setSelection(adapter.getPosition(fatherShownName));
            if(query.getString(viewHolder.HIDE).equals("0")) {
                viewHolder.hide.setText("N");
            }else{
                viewHolder.hide.setText("Y");
            }
        }
    }

    /*TOUR TREATMENT*/
    private void updateTOUR(ViewHolderTour viewHolder) {
        Cursor query = getAllSelectedItemData(POIsContract.TourEntry.CONTENT_URI, TOUR_IDselection);
        fillPOIsCategoriesSpinner(viewHolder.categoryID);
        setDataToTourLayout(query, viewHolder);
        viewHolder.updateTOUR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTourModifications();
                updateTourPOIsModifications();
            }
        });
    }
    public static void deleteButtonTreatment(View view, final String name){
        final ImageView delete = (ImageView) view.findViewById(R.id.delete);
        screenSizeTreatment(delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = tourPOIsNames.indexOf(name);
                tourExistingPOIsNames.remove(name);
                tourPOIsNames.remove(name);
                String id = namesAndIDs.get(name);
                tourExistingPOIsIDs.remove(id);
                tourPOIsIDs.remove(id);
                namesAndIDs.remove(name);
                FragmentActivity activity = (FragmentActivity) rootView.getContext();
                POIsContract.TourPOIsEntry.deleteByTourIdAndPoiID(activity, itemSelectedID, id);
                TourPOIsAdapter.deleteDurationByPosition(position);
                TourPOIsAdapter.setType("updating");
                TourPOIsAdapter adapter = new TourPOIsAdapter(activity, tourPOIsNames);
                viewHolderTour.addedPois.setAdapter(adapter);
            }
        });
    }
    private static void screenSizeTreatment(ImageView delete) {
        DisplayMetrics metrics = new DisplayMetrics();
        FragmentActivity act = (FragmentActivity) rootView.getContext();
        act.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        float scaleFactor = metrics.density;


        //The size of the diagonal in inches is equal to the square root of the height in inches squared plus the width in inches squared.
        float widthDp = widthPixels / scaleFactor;
        float heightDp = heightPixels / scaleFactor;

        float smallestWidth = Math.min(widthDp, heightDp);

        if (smallestWidth >= 1000) {
            delete.setImageResource(R.drawable.ic_remove_circle_black_36dp);
        }
    }
    private void updateTourModifications() {
        ContentValues contentValues = getContentValuesFromDataFromTourInputForm(viewHolderTour);

        int updatedRows = POIsContract.TourEntry.updateByID(getActivity(), contentValues, itemSelectedID);
    }
    private void updateTourPOIsModifications() {
        ContentValues contentValues = new ContentValues();
        int i = 1;
        List<Integer> durationList = TourPOIsAdapter.getDurationList();
        for(String poiName : tourPOIsNames){
            contentValues.clear();
            String poiID = namesAndIDs.get(poiName);
            contentValues.put(POIsContract.TourPOIsEntry.COLUMN_POI_ID, Integer.parseInt(poiID));
            contentValues.put(POIsContract.TourPOIsEntry.COLUMN_TOUR_ID, itemSelectedID);
            contentValues.put(POIsContract.TourPOIsEntry.COLUMN_POI_ORDER, i);
            contentValues.put(POIsContract.TourPOIsEntry.COLUMN_POI_DURATION, durationList.get(i-1));
            if(tourExistingPOIsIDs.contains(poiID)) {
                int updatedRows = POIsContract.TourPOIsEntry.updateByTourIdAndPoiID(getActivity(), contentValues, itemSelectedID, poiID);
            }else{
                Uri insertedUri = POIsContract.TourPOIsEntry.createNewTourPOI(getActivity(), contentValues);
            }
            i++;
        }
        Intent intent = new Intent(getActivity(), LGPCAdminActivity.class);
        startActivity(intent);
    }
    private void setDataToTourLayout(Cursor query, ViewHolderTour viewHolder) {
        if(query.moveToFirst()){
            viewHolder.tourName.setText(query.getString(viewHolder.NAME));
            viewHolder.categoryID.setSelection(adapter.getPosition(getShownNameByCategoryID(query, null, viewHolder, "TOUR")));
            if(query.getString(viewHolder.HIDE).equals("0")) {
                viewHolder.hide.setText("N");
            }else{
                viewHolder.hide.setText("Y");
            }
            int global_interval = query.getInt(viewHolder.INTERVAL);
            viewHolder.global_interval.setText(String.valueOf(global_interval));
            setListOfPOIs();
            setDataToTourPOIsLayout(global_interval);
        }
    }
    private void setListOfPOIs(){
        POISFragment fragment = new POISFragment();
        Bundle args = new Bundle();
        args.putString("createORupdate", "update");
        args.putString("EDITABLE", "ADMIN/TOUR_POIS");
        fragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_tour_pois, fragment).commit();
    }
    private void setDataToTourPOIsLayout(int globalInterval) {
        FragmentActivity fragmentActivity = getActivity();
        Cursor cursor = POIsContract.TourPOIsEntry.getPOIsByTourID(fragmentActivity, itemSelectedID);
        String name, id;
        List<Integer> durationList = new ArrayList<Integer>();
        while (cursor.moveToNext()){
            name = cursor.getString(1);
            id = String.valueOf(cursor.getInt(0));
            durationList.add(cursor.getInt(2));
            tourPOIsIDs.add(id);
            tourExistingPOIsIDs.add(id);
            tourPOIsNames.add(name);
            tourExistingPOIsNames.add(name);
            namesAndIDs.put(name, id);
        }

        TourPOIsAdapter.setGlobalInterval(globalInterval);
        TourPOIsAdapter.setType("updating");
        TourPOIsAdapter.setPOIsDuration(durationList);
        TourPOIsAdapter adapter = new TourPOIsAdapter(fragmentActivity, tourPOIsNames);
        viewHolderTour.addedPois.setAdapter(adapter);

//        for (int i = 0; i < viewHolderTour.addedPois.getCount(); i++) {
//            durationField = (EditText) viewHolderTour.addedPois.getChildAt(i).findViewById(R.id.poi_seconds);
//            durationField.setText(durationList.get(i));
//        }

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(fragmentActivity, android.R.layout.simple_expandable_list_item_1, tourPOIsNames);
//        viewHolder.addedPois.setAdapter(adapter);
    }
    private ViewHolderTour setTOURLayoutSettings(LayoutInflater inflater, ViewGroup container) {
        rootView = inflater.inflate(R.layout.fragment_create_or_update_tour, container, false);
        final ViewHolderTour viewHolder = new ViewHolderTour(rootView);
        viewHolder.createTOUR.setVisibility(View.GONE);
        viewHolder.updateTOUR.setVisibility(View.VISIBLE);
        setCancelComeBackBehaviour(viewHolder.cancel);

        return viewHolder;
    }
    //when, from POIsFragment, we are updating a TOUR and we want to ADD another POI
    public static void setPOItoTourPOIsList(String poiSelected, String completeName) {
        if(!tourPOIsIDs.contains(poiSelected)){
            tourPOIsIDs.add(poiSelected);
            tourPOIsNames.add(completeName);
            namesAndIDs.put(completeName, poiSelected);

            TourPOIsAdapter.setType("updating");
            TourPOIsAdapter.addToDurationList(); //the new POI will initially have a duration of 'general duration' and this method introduces that duration to the list of durations.

            FragmentActivity activity = (FragmentActivity) rootView.getContext();
            TourPOIsAdapter adapter = new TourPOIsAdapter(activity, tourPOIsNames);
            viewHolderTour.addedPois.setAdapter(adapter);
        }else{
            Toast.makeText(rootView.getContext(), "The POI " + completeName + " already exists inside this Tour.", Toast.LENGTH_LONG).show();
        }
    }
    private ContentValues getContentValuesFromDataFromTourInputForm(ViewHolderTour viewHolder){

        String name = "";
        int hide = 0, categoryID = 0;
        name = viewHolder.tourName.getText().toString();
        hide = getHideValueFromInputForm(viewHolder.hide);
        String shownName = getShownNameValueFromInputForm(viewHolder.categoryID);
        categoryID = getFatherIDValueFromInputForm(shownName);

        ContentValues contentValues = new ContentValues();

        contentValues.put(POIsContract.TourEntry.COLUMN_NAME, name);
        contentValues.put(POIsContract.TourEntry.COLUMN_HIDE, hide);
        contentValues.put(POIsContract.TourEntry.COLUMN_CATEGORY_ID, categoryID);

        return contentValues;
    }

    /*OTHER UTILITIES*/
    private int getHideValueFromInputForm(EditText editText){
        final String hide = editText.getText().toString();
        int hideValue = 0;
        if(hide.equals("Y") || hide.equals("y")) {
            hideValue = 1;
        }
        return hideValue;
    }
    private String getShownNameValueFromInputForm(Spinner spinner){
        if(spinner.getSelectedItem() == null || (spinner.getSelectedItem().toString()).equals("NO ROUTE")){
            return "";
        }else{
            return spinner.getSelectedItem().toString();
        }
    }
    private int getFatherIDValueFromInputForm(String shownNameSelected){
        if(shownNameSelected.equals("")){
            return 0;
        }else {
            return Integer.parseInt(spinnerIDsAndShownNames.get(shownNameSelected));
        }
    }
    private Cursor getAllSelectedItemData(Uri uri, String selection){
        return getActivity().getContentResolver().query(uri,
                null, selection, new String[]{itemSelectedID}, null);
    }
    private void setCancelComeBackBehaviour(FloatingActionButton cancel){

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TourPOIsAdapter.getDurationList().clear();
                Intent intent = new Intent(getActivity(), LGPCAdminActivity.class);
                startActivity(intent);
            }
        });
    }


}
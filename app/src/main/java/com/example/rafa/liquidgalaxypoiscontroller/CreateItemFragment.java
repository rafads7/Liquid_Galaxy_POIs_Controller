package com.example.rafa.liquidgalaxypoiscontroller;

import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.rafa.liquidgalaxypoiscontroller.data.POIsContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CreateItemFragment extends android.support.v4.app.Fragment {

    private String creationType;
    private Cursor queryCursor;
    private static View rootView= null;
    private static Map<String, String> spinnerIDsAndShownNames, namesAndIDs;
    private static ArrayList<String> tourPOIsNames, tourPOIsIDs, tourPOIsDuration;
    private static ViewHolderTour viewHolderTour;
    private PopupWindow popupPoiSelected;

    public CreateItemFragment() {
        tourPOIsIDs = new ArrayList<String>();
        tourPOIsNames = new ArrayList<String>();
        tourPOIsDuration = new ArrayList<String>();
        namesAndIDs = new HashMap<String, String>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle extras = getActivity().getIntent().getExtras();
        rootView = null;

        if(extras!=null){
            this.creationType = extras.getString("CREATION_TYPE");
        }

        if(creationType.startsWith("POI")){
            final ViewHolderPoi viewHolder = setPOILayoutSettings(inflater, container);
            viewHolder.createPOI.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createPOI(viewHolder);
                }
            });
        }
        else if(creationType.startsWith("TOUR")){

            setTourLayoutSettings(inflater, container);
            popupItemSelected();
            viewHolderTour.createTOUR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tourID = createTour();
                    addTourPOIsTODataBase(tourID);//TENIR EN COMPTE ELS POIS DE DINS DEL TOUR!!!!!!!!!!!!!!!!!!!!
                }
            });
        }
        else{//CATEGORY
            final ViewHolderCategory viewHolder = setCategoryLayoutSettings(inflater, container);
            viewHolder.createCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   createCategory(viewHolder);
                }
            });
        }
        return rootView;
    }

    public static class ViewHolderPoi {

        public EditText cityET;
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
        public Button createPOI;
        public Button updatePOI;
        public Button cancel;

        public ViewHolderPoi(View rootView) {

            cityET = (EditText) rootView.findViewById(R.id.city);
            visitedPlaceET = (EditText) rootView.findViewById(R.id.visited_place);
            longitudeET = (EditText) rootView.findViewById(R.id.longitude);
            latitudeET = (EditText) rootView.findViewById(R.id.latitude);
            altitudeET = (EditText) rootView.findViewById(R.id.latitude);
            headingET = (EditText) rootView.findViewById(R.id.heading);
            tiltET = (EditText) rootView.findViewById(R.id.tilt);
            rangeET = (EditText) rootView.findViewById(R.id.range);
            altitudeModeET = (EditText) rootView.findViewById(R.id.altitudeMode);
            categoryID = (Spinner) rootView.findViewById(R.id.categoryID_spinner);
            hide = (EditText) rootView.findViewById(R.id.poi_hide);
            createPOI = (Button) rootView.findViewById(R.id.create_poi);
            updatePOI = (Button) rootView.findViewById(R.id.update_poi);
            cancel = (Button) rootView.findViewById(R.id.cancel_come_back);
        }
    }
    public static class ViewHolderTour {

        public EditText tourName;
        public EditText hide;
        public Spinner categoryID;
        public Button createTOUR;
        public Button updateTOUR;
        public Button cancel;
        public ListView addedPois;
        public EditText globalInterval;
//        public DynamicListView addedPois;

        public ViewHolderTour(View rootView) {

            tourName = (EditText) rootView.findViewById(R.id.tour_name);
            hide = (EditText) rootView.findViewById(R.id.tour_hide);
            categoryID = (Spinner) rootView.findViewById(R.id.categoryID_spinner);
            createTOUR = (Button) rootView.findViewById(R.id.create_tour);
            updateTOUR = (Button) rootView.findViewById(R.id.update_tour);
//            addedPois = (DynamicListView) rootView.findViewById(R.id.tour_pois_listview);
            addedPois = (ListView) rootView.findViewById(R.id.tour_pois_listview);
            cancel = (Button) rootView.findViewById(R.id.cancel_come_back);
            globalInterval = (EditText) rootView.findViewById(R.id.pois_interval);
        }
    }
    public static class ViewHolderCategory {

        public EditText categoryName;
        public EditText hide;
        public Spinner fatherID;
        public Button createCategory;
        public Button updateCategory;
        public Button cancel;

        public ViewHolderCategory(View rootView) {

            categoryName = (EditText) rootView.findViewById(R.id.category_name);
            hide = (EditText) rootView.findViewById(R.id.category_hide);
            fatherID = (Spinner) rootView.findViewById(R.id.father_spinner);
            createCategory = (Button) rootView.findViewById(R.id.create_category);
            updateCategory = (Button) rootView.findViewById(R.id.update_category);
            cancel = (Button) rootView.findViewById(R.id.cancel_come_back);
        }


    }

    /*POIs TREATMENT*/
    private void createPOI(ViewHolderPoi viewHolder){
        try {
            ContentValues contentValues = getContentValuesFromDataFromPOIInputForm(viewHolder);

            Uri insertedUri = POIsContract.POIEntry.createNewPOI(getActivity(), contentValues);
            Toast.makeText(getActivity(), insertedUri.toString(), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getActivity(), AdminActivity.class);
            startActivity(intent);
        }catch (NumberFormatException e){
            Toast.makeText(getActivity(),"The attributes 'Longitude, Latitude, Altitude, Heading, Tilt and Range' must have numeric values.", Toast.LENGTH_LONG).show();
        }
    }
    private ContentValues getContentValuesFromDataFromPOIInputForm(ViewHolderPoi viewHolder){

        String city = "", completeName = "", visitedPlace = "", altitudeMode = "";
        float longitude = 0, latitude = 0, altitude = 0, heading = 0, tilt = 0, range = 0;
        int hide = 0, categoryID = 0;

        city = viewHolder.cityET.getText().toString();
        visitedPlace = viewHolder.visitedPlaceET.getText().toString();
        completeName = city + " - " + visitedPlace;
        longitude = Float.parseFloat(viewHolder.longitudeET.getText().toString());
        latitude = Float.parseFloat(viewHolder.latitudeET.getText().toString());
        altitude = Float.parseFloat(viewHolder.altitudeET.getText().toString());
        heading = Float.parseFloat(viewHolder.headingET.getText().toString());
        tilt = Float.parseFloat(viewHolder.tiltET.getText().toString());
        range = Float.parseFloat(viewHolder.rangeET.getText().toString());
        altitudeMode = viewHolder.altitudeModeET.getText().toString();
        hide = getHideValueFromInputForm(viewHolder.hide);

        if(creationType.endsWith("HERE")) {
            categoryID = POISFragment.routeID;
        }else{
            String shownName = getShownNameValueFromInputForm(viewHolder.categoryID);
            categoryID = getFatherIDValueFromInputForm(shownName);
        }


        ContentValues contentValues = new ContentValues();

        contentValues.put(POIsContract.POIEntry.COLUMN_CITY_NAME, city);
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

        return contentValues;
    }
    private ViewHolderPoi setPOILayoutSettings(LayoutInflater inflater, ViewGroup container){
        rootView = inflater.inflate(R.layout.fragment_create_or_update_poi, container, false);
        final ViewHolderPoi viewHolder = new ViewHolderPoi(rootView);
        viewHolder.updatePOI.setVisibility(View.GONE);
        viewHolder.createPOI.setVisibility(View.VISIBLE);

        if(creationType.endsWith("HERE")){
            viewHolder.categoryID.setVisibility(View.GONE);
        }else{
            fillCategorySpinner(viewHolder.categoryID);
        }
        setCancelComeBackBehaviour(viewHolder.cancel);
        return viewHolder;
    }

    /*CATEGORIES TREATMENT*/
    private void createCategory(ViewHolderCategory viewHolder){
        ContentValues contentValues = getContentValuesFromDataFromCategoryInputForm(viewHolder);

        try{
            Uri insertedUri = POIsContract.CategoryEntry.createNewCategory(getActivity(), contentValues);
            Toast.makeText(getActivity(), insertedUri.toString(), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getActivity(), AdminActivity.class);
            startActivity(intent);
        }catch(android.database.SQLException e){
            Toast.makeText(getActivity(), "This category already exists. Modify the attribute 'Name'", Toast.LENGTH_LONG).show();
        }
    }
    private ContentValues getContentValuesFromDataFromCategoryInputForm(ViewHolderCategory viewHolder){
        ContentValues contentValues = new ContentValues();

        String categoryName = viewHolder.categoryName.getText().toString();
        int hideValue = getHideValueFromInputForm(viewHolder.hide);
        int fatherID;
        String shownName = "";
        if(creationType.endsWith("HERE")) {
            fatherID = POISFragment.routeID;
            shownName = POIsContract.CategoryEntry.getShownNameByID(getActivity(), fatherID)
                    + viewHolder.categoryName.getText().toString() + "/";

        }else{
            shownName = getShownNameValueFromInputForm(viewHolder.fatherID);
            fatherID = getFatherIDValueFromInputForm(shownName);
            shownName = shownName + viewHolder.categoryName.getText().toString() + "/";
        }

        contentValues.put(POIsContract.CategoryEntry.COLUMN_NAME, categoryName);
        contentValues.put(POIsContract.CategoryEntry.COLUMN_FATHER_ID, fatherID);
        contentValues.put(POIsContract.CategoryEntry.COLUMN_SHOWN_NAME, shownName);
        contentValues.put(POIsContract.CategoryEntry.COLUMN_HIDE, hideValue);

        return contentValues;
    }
    private ViewHolderCategory setCategoryLayoutSettings(LayoutInflater inflater, ViewGroup container) {
        rootView = inflater.inflate(R.layout.fragment_create_or_update_category, container, false);
        final ViewHolderCategory viewHolder = new ViewHolderCategory(rootView);
        viewHolder.updateCategory.setVisibility(View.GONE);
        viewHolder.createCategory.setVisibility(View.VISIBLE);

        if(creationType.endsWith("HERE")){
            viewHolder.fatherID.setVisibility(View.GONE);
        }else {
            fillCategorySpinner(viewHolder.fatherID);
        }
        setCancelComeBackBehaviour(viewHolder.cancel);
        return viewHolder;
    }

    /*TOUR TREATMENT*/
    private void setTourLayoutSettings(LayoutInflater inflater, ViewGroup container) {
        rootView = inflater.inflate(R.layout.fragment_create_or_update_tour, container, false);
        viewHolderTour = new ViewHolderTour(rootView);
        viewHolderTour.updateTOUR.setVisibility(View.GONE);
        viewHolderTour.createTOUR.setVisibility(View.VISIBLE);
        if(creationType.endsWith("HERE")){
            viewHolderTour.categoryID.setVisibility(View.GONE);
        }else{
            fillCategorySpinner(viewHolderTour.categoryID);
        }
        setCancelComeBackBehaviour(viewHolderTour.cancel);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_tour_pois, new POISFragment(), "ADMIN/TOUR_POIS").commit();
    }
    private int createTour(){
        int tourID = 0;
        try {
            ContentValues contentValues = getContentValuesFromDataFromTourInputForm(viewHolderTour);

            Uri insertedUri = POIsContract.TourEntry.createNewTOUR(getActivity(), contentValues);
            Toast.makeText(getActivity(), insertedUri.toString(), Toast.LENGTH_SHORT).show();
            tourID = POIsContract.TourEntry.getIdByUri(insertedUri);

        }catch (NumberFormatException e){
            Toast.makeText(getActivity(),"Error.", Toast.LENGTH_LONG).show();
        }
        return tourID;
    }
    private ContentValues getContentValuesFromDataFromTourInputForm(ViewHolderTour viewHolder){

        String name = "";
        int hide = 0, categoryID = 0, interval = 0;
        name = viewHolder.tourName.getText().toString();
        hide = getHideValueFromInputForm(viewHolder.hide);
        interval = Integer.parseInt(viewHolder.globalInterval.getText().toString());
        if(creationType.endsWith("HERE")) {
            categoryID = POISFragment.routeID;
        }else{
            String shownName = getShownNameValueFromInputForm(viewHolder.categoryID);
            categoryID = getFatherIDValueFromInputForm(shownName);
        }

        ContentValues contentValues = new ContentValues();

        contentValues.put(POIsContract.TourEntry.COLUMN_NAME, name);
        contentValues.put(POIsContract.TourEntry.COLUMN_HIDE, hide);
        contentValues.put(POIsContract.TourEntry.COLUMN_CATEGORY_ID, categoryID);
        contentValues.put(POIsContract.TourEntry.COLUMN_INTERVAL, interval);

        return contentValues;
    }
    public static void setPOItoTourPOIsList(String poiSelected, String completeName) {
        if(!tourPOIsIDs.contains(poiSelected)){
            tourPOIsIDs.add(poiSelected);
            tourPOIsNames.add(completeName);
            tourPOIsDuration.add("0");
            namesAndIDs.put(completeName, poiSelected);
            FragmentActivity activity = (FragmentActivity) rootView.getContext();


//-----------------
            TourPOIsAdapter.setType("creating");
            TourPOIsAdapter adapter = new TourPOIsAdapter(activity, tourPOIsNames);
            viewHolderTour.addedPois.setAdapter(adapter);
//------------------
//            AlphaInAnimationAdapter animationAdapter = new AlphaInAnimationAdapter(adapter);
//            animationAdapter.setAbsListView(viewHolderTour.addedPois);
//            viewHolderTour.addedPois.enableDragAndDrop();
//            viewHolderTour.addedPois.setDraggableManager(new TouchViewDraggableManager(android.R.id.text1));
            //----------------------
//            StableArrayAdapter adapter = new StableArrayAdapter(activity, R.layout.adapter_textview, tourPOIsNames);
//            viewHolderTour.addedPois.setCheeseList(tourPOIsNames);
//            viewHolderTour.addedPois.setAdapter(adapter);
//            viewHolderTour.addedPois.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }
    }
    private void addTourPOIsTODataBase(int tourID) {

        ContentValues contentValues = new ContentValues();
        EditText sec;
        int i = 1, pois_number = viewHolderTour.addedPois.getCount(), seconds = 0;
        int global_interval = Integer.parseInt(viewHolderTour.globalInterval.getText().toString());

        for(String poiName : tourPOIsNames){
            contentValues.clear();
            if(i <= pois_number) {
                sec = (EditText) viewHolderTour.addedPois.getChildAt(i - 1).findViewById(R.id.poi_seconds);
                if(sec.getText().toString().equals("")){
                    seconds = global_interval;
                }else {
                    try {
                        seconds = Integer.parseInt(sec.getText().toString());
                        if (seconds == 0) {
                            seconds = global_interval;
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(getActivity(), "The duration of each POI must be in seconds (numeric type).", Toast.LENGTH_LONG).show();
                    }
                }
            }
            contentValues.put(POIsContract.TourPOIsEntry.COLUMN_POI_ID, Integer.parseInt(namesAndIDs.get(poiName)));
            contentValues.put(POIsContract.TourPOIsEntry.COLUMN_TOUR_ID, tourID);
            contentValues.put(POIsContract.TourPOIsEntry.COLUMN_POI_ORDER, i);
            contentValues.put(POIsContract.TourPOIsEntry.COLUMN_POI_DURATION, seconds);
            Uri insertedUri = POIsContract.TourPOIsEntry.createNewTourPOI(getActivity(), contentValues);
            Toast.makeText(getActivity(), insertedUri.toString(), Toast.LENGTH_SHORT).show();
            i++;
        }
        Intent intent = new Intent(getActivity(), AdminActivity.class);
        startActivity(intent);
    }
    private void popupItemSelected(){
        viewHolderTour.addedPois.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String name = (String) parent.getItemAtPosition(position);
                View popup = createPopup();
                cancelButtonTreatment(popup);
                deleteButtonTreatment(popup, name);
                return true;
            }
        });
    }
    private void deleteButtonTreatment(View view, final String name){
        final Button delete = (Button) view.findViewById(R.id.delete_poi);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tourPOIsNames.remove(name);
                String id = namesAndIDs.get(name);
                tourPOIsIDs.remove(id);
                namesAndIDs.remove(name);

                FragmentActivity activity = (FragmentActivity) rootView.getContext();
                TourPOIsAdapter.setType("creating");
                TourPOIsAdapter adapter = new TourPOIsAdapter(activity, tourPOIsNames);
                viewHolderTour.addedPois.setAdapter(adapter);
//                FragmentActivity activity = (FragmentActivity) rootView.getContext();
//                ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_expandable_list_item_1, tourPOIsNames);
//                viewHolderTour.addedPois.setAdapter(adapter);
                popupPoiSelected.dismiss();
            }
        });
    }
    private void cancelButtonTreatment(View view){

        final Button cancelButton = (Button) view.findViewById(R.id.cancel_poi_selection);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupPoiSelected.dismiss();
            }
        });
    }
    private View createPopup(){
        final LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popup_tour_poi_selected, null);

        popupPoiSelected = new PopupWindow(popupView, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        popupPoiSelected.setTouchable(true);
        popupPoiSelected.setFocusable(true);
        popupPoiSelected.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        return popupView;
    }

    /*OTHER UTILITIES*/
    private void fillCategorySpinner(Spinner spinner){

        List<String> list = new ArrayList<String>();
        list.add("NO ROUTE");
        spinnerIDsAndShownNames = new HashMap<String, String>();

        //We get all the categories IDs and ShownNames
        queryCursor = POIsContract.CategoryEntry.getIDsAndShownNamesOfAllCategories(getActivity());

        while(queryCursor.moveToNext()){
            spinnerIDsAndShownNames.put(queryCursor.getString(1), String.valueOf(queryCursor.getInt(0)));
            list.add(queryCursor.getString(1));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
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
    private void setCancelComeBackBehaviour(Button cancel){

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent admin = new Intent(getActivity(), AdminActivity.class);
                startActivity(admin);
            }
        });
    }
}
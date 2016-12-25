package fragments;


import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.contacts.contactsmap.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import models.ContactModel;
import models.MessageEvent;
import models.UpdateMapEvent;

/**
 * Created by haseeb on 23/12/16.
 */
public class ContactMap extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    ArrayList<ContactModel> data = new ArrayList<ContactModel>();
    EventBus eventBus = EventBus.getDefault();
    LatLng cameralocation = null;


    @Override
    public void onStart() {
        super.onStart();
        eventBus.register(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contacsmap_fragment_layout, container, false);
        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        Bundle b = getArguments();
        data = b.getParcelableArrayList("DATA");
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }


        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                googleMap.setMyLocationEnabled(true);

                // For dropping a marker at a point on the Map
                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i).getLongitude() != null) {
                        if (cameralocation == null) {
                            cameralocation = new LatLng(data.get(i).getLatitude(), data.get(i).getLongitude());
                        }
                        System.out.println("LATLONG : " + data.get(i).getLatitude() + "-----" + data.get(i).getLongitude());
                        googleMap.addMarker(new MarkerOptions().position(new LatLng(data.get(i).getLatitude(), data.get(i).getLongitude())).title(data.get(i).getName()).snippet(data.get(i).getPhone()));
                    }
                }
                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(cameralocation).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        eventBus.unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    public void onEvent(final MessageEvent event) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < event.getData().size(); i++) {
                    if (event.getData().get(i).getLongitude() != null) {
                        googleMap.addMarker(new MarkerOptions().position(new LatLng(event.getData().get(i).getLatitude(), event.getData().get(i).getLongitude())).title(event.getData().get(i).getName()).snippet(event.getData().get(i).getPhone()));
                    }
                }
            }
        });

    }

    public void onEvent(final UpdateMapEvent event) {
        System.out.println("EVENT : "+event.getPosition());
        getActivity().runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(data.get(event.getPosition()).getLatitude(), data.get(event.getPosition()).getLongitude())).zoom(12).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    }
                }
        );

    }

}

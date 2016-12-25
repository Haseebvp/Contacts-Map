package adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import fragments.AllContacts;
import fragments.ContactMap;
import models.ContactModel;

/**
 * Created by haseeb on 23/12/16.
 */

public class Pager extends FragmentStatePagerAdapter {
    //integer to count number of tabs
    int tabCount;

    private String[] tabTitles = {"All Contacts", "Contacts Map"};
    ArrayList<ContactModel> data = new ArrayList<ContactModel>();

    //Constructor to the class
    public Pager(FragmentManager fm, int tabCount, ArrayList<ContactModel> data ) {
        super(fm);
        //Initializing tab count
        this.tabCount = tabCount;
        this.data = data;
    }

    //Overriding method getItem
    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        switch (position) {
            case 0:
                AllContacts tab1 = new AllContacts();
                Bundle data1 = new Bundle();
                data1.putParcelableArrayList("DATA", data);
                tab1.setArguments(data1);
                return tab1;


            case 1:

                ContactMap tab2 = new ContactMap();
                Bundle data2 = new Bundle();
                data2.putParcelableArrayList("DATA", data);
                tab2.setArguments(data2);
                return tab2;

            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    //Overriden method getCount to get the number of tabs
    @Override
    public int getCount() {
        return tabTitles.length;
    }

}

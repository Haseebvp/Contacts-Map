package fragments;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.contacts.contactsmap.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ViewDecoraters.CustomItemDecorator;
import adapters.ContactAdapter;
import de.greenrobot.event.EventBus;
import models.ContactModel;
import models.MessageEvent;

/**
 * Created by haseeb on 23/12/16.
 */
public class AllContacts extends Fragment {

    RecyclerView recyclerView;
    ContactAdapter adapter;
    ArrayList<ContactModel> data = new ArrayList<ContactModel>();
    ArrayList<ContactModel> scrolldata = new ArrayList<ContactModel>();
    EventBus eventBus = EventBus.getDefault();
    LinearLayoutManager layoutManager;
    int scrollcount = 20;
    int pageNo = 1;
    boolean loading = false;
    boolean phonebookstatus = false;
    Handler handler = new Handler();


    @Override
    public void onStart() {
        super.onStart();
        eventBus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        eventBus.unregister(this);
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
        View view = inflater.inflate(R.layout.allcontacts_fragment_layout, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);

        Bundle b = getArguments();
        data = b.getParcelableArrayList("DATA");
        System.out.println("DATATATATAT : " + data.size());
        if (data.size() > 20) {
            scrolldata.addAll(data.subList(0, scrollcount * pageNo));
        } else {
            scrolldata.addAll(data.subList(0, data.size()));
        }

        adapter = new ContactAdapter(scrolldata, getContext());
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new CustomItemDecorator(ContextCompat.getDrawable(getContext(), R.drawable.line_divider)));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                if (!loading) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        if (data.size() > scrolldata.size()) {
                            System.out.println("Scrolllll : " + data.size() + "---" + scrollcount + "---" + scrolldata.size());
                            scrolldata.add(null);
                            adapter.notifyItemInserted(scrolldata.size());
                            loading = true;
                            pageNo += 1;
                            loadMoreItems();
                        }
                    }
                }
            }

        });
        return view;
    }


    private void loadMoreItems() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (loading) {
                    scrolldata.remove(scrolldata.size()-1);
                    if (data.size() > scrollcount * pageNo) {
                        System.out.println("Scroll : inside if");
                        scrolldata.addAll(data.subList(scrollcount * (pageNo-1), scrollcount * pageNo));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    } else {
                        System.out.println("Scroll : inside else");
                        scrolldata.addAll(data.subList(scrollcount * (pageNo-1), data.size()));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }

                    loading = false;
                }
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void onEvent(MessageEvent event) {
//        Toast.makeText(getActivity(), event.getData().toString(), Toast.LENGTH_SHORT).show();
        if (!loading) {
            data.addAll(event.getData());
//            adapter.notifyDataSetChanged();
        }
    }
}

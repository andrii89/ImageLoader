package com.test.android.applicationa;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.test.android.applicationa.database.AppDatabase;
import com.test.android.applicationa.database.LinkData;

import java.util.List;

public class HistoryFragment extends Fragment {

    private final String EXTRA_LINK_ID = "link_id";

    private AppDatabase mDb;

    private RecyclerView mRecyclerView;

    public static HistoryFragment newInstance(){

        HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.history_tab, container, false);

        mRecyclerView = view.findViewById(R.id.history_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setRetainInstance(true);

        mDb = AppDatabase.getInstance(getContext());

        setupAdapter(1);

        return view;
    }

    class LinkHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        TextView mLinkTextView;
        LinkData linkData;

        public LinkHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);

            mLinkTextView = itemView.findViewById(R.id.link_text_view);
        }

        @Override
        public void onClick(View view){
            ComponentName cn = new ComponentName("com.test.android.applicationb", "com.test.android.applicationb.MainActivity");
            Intent intent = new Intent();
            intent.setComponent(cn);
            intent.putExtra(EXTRA_LINK_ID, linkData.getId());
            try{
                getContext().startActivity(intent);
            } catch (Exception ex) {
                Toast.makeText(getActivity(), "Unable to launch App B", Toast.LENGTH_SHORT).show();
            }
        }

        public void bindArticle(LinkData item){
            linkData = item;
            mLinkTextView.setText(item.getLink());
            itemView.setBackgroundColor(getStatusColor(item.getStatus()));
        }
    }

    private class ResultAdapter extends RecyclerView.Adapter<LinkHolder>{
        private List<LinkData> mLinks;

        public ResultAdapter(List<LinkData> links){
            mLinks = links;
        }

        @Override
        public LinkHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.links_list_item, parent, false);
            return new LinkHolder(view);
        }

        @Override
        public void onBindViewHolder(LinkHolder holder, int position){
            LinkData listItem = mLinks.get(position);
            holder.bindArticle(listItem);
        }

        @Override
        public int getItemCount(){
            return mLinks.size();
        }
    }

    private void setupAdapter(int query){
        if(isAdded()){
            LiveData<List<LinkData>> linkList;
            switch (query){
                case 1:
                    linkList = mDb.linkDao().loadLinksSync();
                    break;
                case 2:
                    linkList = mDb.linkDao().loadLinksSortedByStatusSync();
                    break;
                case 3:
                    linkList = mDb.linkDao().loadLinksSortedByTimeSync();
                    break;
                default:
                    linkList = mDb.linkDao().loadLinksSync();
            }
            linkList.observe(this, new Observer<List<LinkData>>() {
                @Override
                public void onChanged(@Nullable List<LinkData> links) {
                    mRecyclerView.setAdapter(new ResultAdapter(links));
                }
            });
        }
    }

    private int getStatusColor(int status) {
        int priorityColor = 0;

        switch (status) {
            case 1:
                priorityColor = ContextCompat.getColor(getContext(), R.color.colorGreen);
                break;
            case 2:
                priorityColor = ContextCompat.getColor(getContext(), R.color.colorRed);
                break;
            case 3:
                priorityColor = ContextCompat.getColor(getContext(), R.color.colorGrey);
                break;
            default:
                break;
        }
        return priorityColor;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.top_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_sortByStatus:
                setupAdapter(2);
                return true;
            case R.id.menu_item_sortByTime:
                setupAdapter(3);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

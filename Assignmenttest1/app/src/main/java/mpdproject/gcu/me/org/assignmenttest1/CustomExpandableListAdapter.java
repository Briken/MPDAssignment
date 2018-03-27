package mpdproject.gcu.me.org.assignmenttest1;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * David Hesketh Mobile Platform Development Matric No:S1437170
 */

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listHashMap;
    private ArrayList<Item> items;

    public CustomExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<String>> listHashMap, ArrayList<Item> items) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listHashMap = listHashMap;
        this.items = items;
    }

    @Override
    public int getGroupCount() {
        return listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return listHashMap.get(listDataHeader.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return listDataHeader.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return listHashMap.get(listDataHeader.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        String headerTitle = (String)getGroup(i);
        if (view == null)
        {
            LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view  = inflater.inflate(R.layout.list_group, null);
        }

        TextView headerView = (TextView)view.findViewById(R.id.headerView);
        headerView.setTypeface(null, Typeface.BOLD);
        headerView.setText(headerTitle);
        for(Item item : items)
        {
            if (item.title == headerTitle)
            {
                headerView.setBackgroundColor(item.activeColour);
            }
        }
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        final String  childText = (String)getChild(i,i1);
        if (view == null)
        {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.expanded_list_item, null);
        }
        TextView childView = (TextView) view.findViewById(R.id.childlist_item);
        childView.setText(childText);
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}

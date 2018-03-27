package mpdproject.gcu.me.org.assignmenttest1;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * David Hesketh Mobile Platform Development Matric No:S1437170
 */

public class ItemListAdapter extends BaseAdapter
{
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Item> mDataSource;
    private int severityGreen;
    private int severityYellow;
    private int severityRed;
    public ItemListAdapter(Context context, ArrayList<Item> items)
    {
        mContext = context;
        mDataSource = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        severityGreen = mContext.getResources().getColor(R.color.green);
        severityYellow = mContext.getResources().getColor(R.color.yellow);
        severityRed = mContext.getResources().getColor(R.color.red);
    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public Item getItem(int i) {
        return mDataSource.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if (convertView == null)
        {
            View rowView = mInflater.inflate(R.layout.item_list_view, viewGroup, false);
            return rowView;
        }
        Item currentItem = (Item) getItem(position);
        TextView textViewItemTitle = (TextView)
                convertView.findViewById(R.id.text_view_item_title);
        TextView textViewItemDescription = (TextView)
                convertView.findViewById(R.id.text_view_item_description);
        if (currentItem.workDuration != null)
        {
            if (currentItem.workDuration <= 7)
            {
                textViewItemTitle.setBackgroundColor(severityGreen);
            }
            if (currentItem.workDuration > 7 && currentItem.workDuration <= 14)
            {
                textViewItemTitle.setBackgroundColor(severityYellow);
            }
            if (currentItem.workDuration > 14)
            {
                textViewItemTitle.setBackgroundColor(severityRed);
            }
        }
        textViewItemTitle.setText(currentItem.GetTitle());
        textViewItemDescription.setText(currentItem.GetDescription());

        return convertView;
    }


}

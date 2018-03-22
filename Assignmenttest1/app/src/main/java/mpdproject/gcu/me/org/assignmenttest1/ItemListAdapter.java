package mpdproject.gcu.me.org.assignmenttest1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by David Hesketh on 20/03/2018.
 */

public class ItemListAdapter extends BaseAdapter
{
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Item> mDataSource;

    public ItemListAdapter(Context context, ArrayList<Item> items)
    {
        mContext = context;
        mDataSource = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public Object getItem(int i) {
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

        textViewItemTitle.setText(currentItem.GetTitle());
        textViewItemDescription.setText(currentItem.GetDescription());

        return convertView;
    }


}

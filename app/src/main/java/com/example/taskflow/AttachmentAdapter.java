package com.example.taskflow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class AttachmentAdapter extends BaseAdapter {

    public interface Listener {
        void onPreview(AttachmentLink link);

        void onRemove(AttachmentLink link);
    }

    private final Context context;
    private final List<AttachmentLink> items;
    private final Listener listener;

    public AttachmentAdapter(Context context, List<AttachmentLink> items, Listener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public AttachmentLink getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class Holder {
        ImageView thumb;
        TextView name;
        TextView type;
        View preview;
        ImageButton remove;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder h;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_attachment, parent, false);
            h = new Holder();
            h.thumb = convertView.findViewById(R.id.ivAttachmentThumb);
            h.name = convertView.findViewById(R.id.tvAttachmentName);
            h.type = convertView.findViewById(R.id.tvAttachmentType);
            h.preview = convertView.findViewById(R.id.btnPreview);
            h.remove = convertView.findViewById(R.id.btnRemoveAttachment);
            convertView.setTag(h);
        } else {
            h = (Holder) convertView.getTag();
        }

        AttachmentLink link = items.get(position);
        CatalogItem item = CatalogItem.find(link.getCatalogId());
        if (item != null && item.isImage()) {
            h.thumb.setImageResource(item.getResId());
            h.thumb.setScaleType(ImageView.ScaleType.CENTER_CROP);
            h.name.setText(item.getDisplayName());
            h.type.setText(R.string.type_image);
        } else {
            h.thumb.setImageResource(R.drawable.ic_file);
            h.thumb.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            h.name.setText(item != null ? item.getDisplayName() : link.getResourceRef());
            h.type.setText(R.string.type_text);
        }

        h.preview.setOnClickListener(v -> listener.onPreview(link));
        h.remove.setOnClickListener(v -> listener.onRemove(link));
        return convertView;
    }
}

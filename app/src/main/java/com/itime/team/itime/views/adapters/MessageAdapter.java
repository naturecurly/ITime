/*
 * Copyright (C) 2016 Yorkfine Chan <yorkfinechan@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.itime.team.itime.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.itime.team.itime.R;
import com.itime.team.itime.model.ParcelableMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xuhui Chen (yorkfine) on 23/04/16.
 */
public class MessageAdapter extends RecyclerSwipeAdapter<MessageAdapter.SimpleViewHolder> {

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        TextView mMessageTitle;
        TextView mMessageTime;
        TextView mMessageBody;
        TextView buttonDelete;


        SwipeLayout swipeLayout;


        public SimpleViewHolder(View itemView) {
            super(itemView);
            mMessageTitle = (TextView) itemView.findViewById(R.id.message_title);
            mMessageTime = (TextView) itemView.findViewById(R.id.message_time);
            mMessageBody = (TextView) itemView.findViewById(R.id.message_body);

            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            buttonDelete = (TextView) itemView.findViewById(R.id.delete);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        // delete button click interface
        void onItemDeleteClick(View view, int position);
    }

    private List<ParcelableMessage> messageData;
    private List<ParcelableMessage> unReadMessageData;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;


    /* show unread or all messages */
    private boolean isShowAll = false;

    public MessageAdapter(Context context, List<ParcelableMessage> messages, boolean showAll) {
        mContext = context;
        messageData = messages;
        isShowAll = showAll;
        setUnReadMessageData(messageData);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    private void setUnReadMessageData(List<ParcelableMessage> messageData) {
        if (unReadMessageData == null) {
            unReadMessageData = new ArrayList<>();
        } else {
            unReadMessageData.clear();
        }
        for (ParcelableMessage m : messageData) {
            if (!m.ifRead) {
                unReadMessageData.add(m);
            }
        }
    }

    public int getUnreadMessageCount() {
        return unReadMessageData.size();
    }

    public synchronized void loadMessages(List<ParcelableMessage> messageData) {
        this.messageData = messageData;
        setUnReadMessageData(messageData);
        notifyDataSetChanged();
    }

    public boolean unRead(ParcelableMessage message) {
        boolean isRemove = false;
        if (unReadMessageData != null) {
            // remove from the unread message data
            int position = unReadMessageData.indexOf(message);
            isRemove = unReadMessageData.remove(message);
            if (isRemove) {
                // mark message as read
                message.ifRead = true;
                // if current page is all message page, show item change animation
                // else show remove animation
                if (isShowAll) {
                    int pos = messageData.indexOf(message);
                    if (pos >= 0) {
                        notifyItemChanged(pos);
                    }
                } else {
                    notifyItemRemoved(position);
                }
            }
        }
        return isRemove;
    }


    // called when message is successfully deleted in the server
    // do not call under other conditions since it just remove the view and data locally.
    public void deleteMessage(View view, int position) {
        // use message object in the view to get the position because position parameter maybe
        // invalidated due to asynchronous called of this method
        final ParcelableMessage message = (ParcelableMessage) view.getTag();
        int pos1 = messageData.indexOf(message);
        int pos2 = unReadMessageData.indexOf(message);
        messageData.remove(message);
        unReadMessageData.remove(message);
        // even though the postion from the parameter is invalidated, getSwipeLayoutResourceId return
        // the same id that matches tag object in the view. In another word, The swipeLayout is
        // independent to the position.
        mItemManger.removeShownLayouts((SwipeLayout) view.getTag(getSwipeLayoutResourceId(position)));
        if (isShowAll) {
            if (pos1 >= 0) {
                notifyItemRemoved(pos1);
                notifyItemRangeChanged(pos1, messageData.size());
                mItemManger.closeAllItems();
            }
        } else {
            if (pos2 >= 0) {
                notifyItemRemoved(pos2);
                notifyItemRangeChanged(pos2, unReadMessageData.size());
                mItemManger.closeAllItems();
            }

        }
        Toast.makeText(mContext, "Deleted message: " + message.messageTitle + "!", Toast.LENGTH_SHORT).show();


    }

    public void setShowAll(boolean showAll) {
        isShowAll = showAll;
        notifyDataSetChanged();
    }

    public boolean isShowAll() {
        return isShowAll;
    }

    public Object getItem(int position) {
        return isShowAll ? messageData.get(position) : unReadMessageData.get(position);
    }


    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_message_list_cell, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
        final List<ParcelableMessage> mDataset = isShowAll ? messageData : unReadMessageData;
        final ParcelableMessage message = mDataset.get(position);
        viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, viewHolder.swipeLayout.findViewWithTag("delete_bottom"));

        // item click
        if (mOnItemClickListener != null) {
            viewHolder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(v.getContext(), "onItemSelected: " + viewHolder.mMessageTitle.getText().toString(), Toast.LENGTH_SHORT).show();
                    int pos = viewHolder.getLayoutPosition();
                    viewHolder.itemView.setTag(message);
                    mOnItemClickListener.onItemClick(viewHolder.itemView, pos);
                }
            });

            // delete button click
            viewHolder.buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = viewHolder.getLayoutPosition();
                    viewHolder.itemView.setTag(message);
                    viewHolder.itemView.setTag(getSwipeLayoutResourceId(pos), viewHolder.swipeLayout);
                    mOnItemClickListener.onItemDeleteClick(viewHolder.itemView, pos);
                }
            });
        }


        if (message.ifRead) {
            viewHolder.swipeLayout.getSurfaceView().setBackgroundColor(Color.GRAY);
        } else {
            viewHolder.swipeLayout.getSurfaceView().setBackgroundColor(Color.WHITE);
        }

        viewHolder.mMessageTitle.setText(message.messageTitle);
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createTime = formatter.format(message.createdTime);
        viewHolder.mMessageTime.setText(createTime);
        viewHolder.mMessageBody.setText(message.messageSubtitle);

        mItemManger.bindView(viewHolder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return isShowAll ? messageData.size() : unReadMessageData.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }
}

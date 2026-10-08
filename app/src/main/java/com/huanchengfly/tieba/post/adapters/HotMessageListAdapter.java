package com.huanchengfly.tieba.post.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.core.widget.TextViewCompat;

import com.huanchengfly.tieba.post.api.models.web.HotMessageListBean;
import com.huanchengfly.tieba.post.R;
import com.huanchengfly.tieba.post.activities.HotTopicActivity;
import com.huanchengfly.tieba.post.utils.Util;
import com.othershe.baseadapter.ViewHolder;
import com.othershe.baseadapter.base.CommonBaseAdapter;

public class HotMessageListAdapter extends CommonBaseAdapter<HotMessageListBean.HotMessageRetBean> {
    public HotMessageListAdapter(Context context) {
        super(context, null, false);
        addHeaderView(Util.inflate(context, R.layout.header_hot_message_list));
        setOnItemClickListener((viewHolder, hotMessageRetBean, position) -> {
            Intent intent = new Intent(mContext, HotTopicActivity.class)
                    .putExtra(HotTopicActivity.EXTRA_TOPIC_ID, hotMessageRetBean.getMulId())
                    .putExtra(HotTopicActivity.EXTRA_TOPIC_NAME, hotMessageRetBean.getMulName());
            if (hotMessageRetBean.getTopicInfo() != null) {
                intent.putExtra(HotTopicActivity.EXTRA_TOPIC_DESC, hotMessageRetBean.getTopicInfo().getTopicDesc());
            }
            mContext.startActivity(intent);
        });
    }

    @Override
    protected void convert(ViewHolder viewHolder, HotMessageListBean.HotMessageRetBean hotMessageRetBean, int position) {
        viewHolder.setText(R.id.hot_order, String.valueOf(position + 1));
        viewHolder.setText(R.id.hot_title, hotMessageRetBean.getMulName());
        viewHolder.setText(R.id.hot_desc, hotMessageRetBean.getTopicInfo().getTopicDesc());
        TextView textView = viewHolder.getView(R.id.hot_order);
        if (position > 2) {
            TextViewCompat.setTextAppearance(textView, R.style.TextAppearance_Bold);
            textView.setTextColor(mContext.getResources().getColor(R.color.tieba));
        } else {
            TextViewCompat.setTextAppearance(textView, R.style.TextAppearance_Bold_Italic);
            textView.setTextColor(mContext.getResources().getColor(R.color.red_accent));
        }
        if (position > 2 || hotMessageRetBean.getTopicInfo().getTopicDesc() == null) {
            viewHolder.setVisibility(R.id.hot_desc, View.GONE);
        } else {
            viewHolder.setVisibility(R.id.hot_desc, View.VISIBLE);
        }
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_hot_message_list;
    }
}

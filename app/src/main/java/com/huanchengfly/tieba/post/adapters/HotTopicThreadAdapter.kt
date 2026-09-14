package com.huanchengfly.tieba.post.adapters

import android.content.Context
import android.text.TextUtils
import android.text.format.DateUtils
import android.view.View
import com.huanchengfly.tieba.post.R
import com.huanchengfly.tieba.post.api.models.web.HotTopicMainBean
import com.huanchengfly.tieba.post.utils.NavigationHelper
import com.huanchengfly.tieba.post.utils.Util
import com.othershe.baseadapter.ViewHolder
import com.othershe.baseadapter.base.CommonBaseAdapter

class HotTopicThreadAdapter(context: Context) : CommonBaseAdapter<HotTopicMainBean.ThreadBean>(context, null, true) {
    private val navigationHelper = NavigationHelper.newInstance(context)
    private val headerView: View = Util.inflate(context, R.layout.header_hot_topic)

    init {
        addHeaderView(headerView)
        setLoadingView(R.layout.layout_footer_loading)
        setLoadEndView(R.layout.layout_footer_loadend)
        setLoadFailedView(R.layout.layout_footer_load_failed)
    }

    fun bindTopic(topicName: String?, topicDesc: String?, discussNum: String?) {
        headerView.findViewById<android.widget.TextView>(R.id.hot_topic_title).text = topicName
        val descView = headerView.findViewById<android.widget.TextView>(R.id.hot_topic_desc)
        if (TextUtils.isEmpty(topicDesc)) {
            descView.visibility = View.GONE
        } else {
            descView.visibility = View.VISIBLE
            descView.text = topicDesc
        }
        val discussView = headerView.findViewById<android.widget.TextView>(R.id.hot_topic_discuss)
        if (TextUtils.isEmpty(discussNum)) {
            discussView.visibility = View.GONE
        } else {
            discussView.visibility = View.VISIBLE
            discussView.text = mContext.getString(R.string.tip_hot_topic_discuss_num, discussNum)
        }
    }

    override fun convert(viewHolder: ViewHolder, threadBean: HotTopicMainBean.ThreadBean, position: Int) {
        viewHolder.setOnClickListener(R.id.item_hot_topic_thread) {
            val tid = threadBean.threadId ?: ""
            android.util.Log.d("HotTopicAct", "click thread tid=$tid")
            if (!TextUtils.isEmpty(tid)) {
                navigationHelper.navigationByData(NavigationHelper.ACTION_URL, "https://tieba.baidu.com/mo/q/thread_page?kz=" + tid)
            }
        }
        viewHolder.setText(R.id.item_hot_topic_thread_title, threadBean.title)
        val contentView = viewHolder.getView<android.widget.TextView>(R.id.item_hot_topic_thread_content)
        if (TextUtils.isEmpty(threadBean.abstracts)) {
            contentView.visibility = View.GONE
        } else {
            contentView.visibility = View.VISIBLE
            contentView.text = threadBean.abstracts
        }
        viewHolder.setText(R.id.item_hot_topic_thread_forum, mContext.getString(R.string.tip_forum_name, threadBean.forumName ?: ""))
        viewHolder.setText(R.id.item_hot_topic_thread_info, buildInfo(threadBean))
    }

    private fun buildInfo(threadBean: HotTopicMainBean.ThreadBean): String {
        val replyTip = mContext.getString(R.string.tip_hot_topic_thread_reply, threadBean.postNum ?: "0")
        val createTime = threadBean.createTime
        if (TextUtils.isEmpty(createTime)) {
            return replyTip
        }
        return try {
            val relative = DateUtils.getRelativeTimeSpanString(createTime!!.toLong() * 1000L)
            "$replyTip · $relative"
        } catch (e: NumberFormatException) {
            replyTip
        }
    }

    override fun getItemLayoutId(): Int = R.layout.item_hot_topic_thread
}

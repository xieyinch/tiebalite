package com.huanchengfly.tieba.post.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.huanchengfly.tieba.post.R
import com.huanchengfly.tieba.post.adapters.HotTopicThreadAdapter
import com.huanchengfly.tieba.post.api.TiebaApi
import com.huanchengfly.tieba.post.api.models.web.HotTopicBean
import com.huanchengfly.tieba.post.api.models.web.HotTopicThreadBean
import com.huanchengfly.tieba.post.components.MyLinearLayoutManager
import com.huanchengfly.tieba.post.components.dividers.CommonDivider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HotTopicActivity : BaseActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HotTopicThreadAdapter

    private var topicId: String = ""
    private var topicName: String = ""
    private var yurenRand = 0
    private var pmyTopicExt: String = ""
    private var page = 1
    private var endReached = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hot_topic)
        topicId = intent.getStringExtra(EXTRA_TOPIC_ID) ?: ""
        topicName = intent.getStringExtra(EXTRA_TOPIC_NAME) ?: ""
        val topicDesc = intent.getStringExtra(EXTRA_TOPIC_DESC)

        toolbar = findViewById<Toolbar>(R.id.toolbar)
        refreshLayout = findViewById<SwipeRefreshLayout>(R.id.refresh)
        recyclerView = findViewById<RecyclerView>(R.id.recycler_view)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = if (topicName.isEmpty()) getString(R.string.title_hot_message) else topicName
        }

        recyclerView.layoutManager = MyLinearLayoutManager(this)
        recyclerView.addItemDecoration(CommonDivider(this, LinearLayoutManager.VERTICAL, R.drawable.drawable_divider_1dp))

        adapter = HotTopicThreadAdapter(this)
        adapter.bindTopic(topicName, topicDesc, null)
        adapter.setOnLoadMoreListener { isReload -> loadMore(isReload) }
        recyclerView.adapter = adapter

        refreshLayout.setOnRefreshListener { refresh() }

        refresh()
        loadTopicInfo()
    }

    private fun loadTopicInfo() {
        if (topicId.isEmpty()) {
            return
        }
        TiebaApi.getInstance().hotTopic(topicId, topicName).enqueue(object : Callback<HotTopicBean> {
            override fun onResponse(call: Call<HotTopicBean>, response: Response<HotTopicBean>) {
                val data = response.body()?.data ?: return
                yurenRand = data.yurenRand
                pmyTopicExt = data.pmyTopicExt ?: ""
                val info = data.topicInfo?.ret?.firstOrNull() ?: return
                supportActionBar?.title = info.topicName ?: topicName
                adapter.bindTopic(info.topicName ?: topicName, info.topicDesc, info.discussNum ?: info.realDiscussNum)
            }

            override fun onFailure(call: Call<HotTopicBean>, t: Throwable) {
            }
        })
    }

    private fun refresh() {
        page = 1
        endReached = false
        refreshLayout.isRefreshing = true
        adapter.reset()
        requestThread()
    }

    private fun loadMore(isReload: Boolean) {
        if (endReached) {
            adapter.loadEnd()
            return
        }
        if (!isReload) {
            page += 1
        }
        requestThread()
    }

    private fun requestThread() {
        if (topicId.isEmpty()) {
            refreshLayout.isRefreshing = false
            adapter.loadEnd()
            return
        }
        TiebaApi.getInstance().hotTopicThread(topicId, yurenRand, topicName, pmyTopicExt, page, PAGE_SIZE, "").enqueue(object : Callback<HotTopicThreadBean> {
            override fun onResponse(call: Call<HotTopicThreadBean>, response: Response<HotTopicThreadBean>) {
                refreshLayout.isRefreshing = false
                val list = response.body()?.data?.threadList ?: emptyList()
                if (page <= 1) {
                    adapter.setNewData(list)
                } else {
                    adapter.setLoadMoreData(list)
                }
                if (list.size < PAGE_SIZE) {
                    endReached = true
                    adapter.loadEnd()
                }
            }

            override fun onFailure(call: Call<HotTopicThreadBean>, t: Throwable) {
                refreshLayout.isRefreshing = false
                if (page <= 1) {
                    Toast.makeText(this@HotTopicActivity, t.message, Toast.LENGTH_SHORT).show()
                } else {
                    adapter.loadFailed()
                }
            }
        })
    }

    companion object {
        const val EXTRA_TOPIC_ID = "topic_id"
        const val EXTRA_TOPIC_NAME = "topic_name"
        const val EXTRA_TOPIC_DESC = "topic_desc"
        private const val PAGE_SIZE = 30
    }
}

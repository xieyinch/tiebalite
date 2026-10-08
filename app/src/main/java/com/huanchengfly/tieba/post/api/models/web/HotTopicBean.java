package com.huanchengfly.tieba.post.api.models.web;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HotTopicBean extends WebBaseBean<HotTopicBean.HotTopicDataBean> {
    public static class HotTopicDataBean {
        @SerializedName("pmy_topic_ext")
        private String pmyTopicExt;
        @SerializedName("yuren_rand")
        private int yurenRand;
        @SerializedName("topic_info")
        private TopicInfoBean topicInfo;

        public TopicInfoBean getTopicInfo() {
            return topicInfo;
        }

        public String getPmyTopicExt() {
            return pmyTopicExt;
        }

        public int getYurenRand() {
            return yurenRand;
        }
    }

    public static class TopicInfoBean {
        private List<TopicInfoRetBean> ret;

        public List<TopicInfoRetBean> getRet() {
            return ret;
        }
    }

    public static class TopicInfoRetBean {
        @SerializedName("create_time")
        private String createTime;
        @SerializedName("discuss_num")
        private String discussNum;
        @SerializedName("hot_value")
        private String hotValue;
        @SerializedName("topic_id")
        private String topicId;
        @SerializedName("topic_name")
        private String topicName;
        @SerializedName("topic_desc")
        private String topicDesc;
        private String tids;
        @SerializedName("real_discuss_num")
        private String realDiscussNum;
        private TopicInfoRetExtraBean extra;

        public String getCreateTime() {
            return createTime;
        }

        public String getDiscussNum() {
            return discussNum;
        }

        public String getHotValue() {
            return hotValue;
        }

        public String getTopicId() {
            return topicId;
        }

        public String getTopicName() {
            return topicName;
        }

        public String getTopicDesc() {
            return topicDesc;
        }

        public String getTids() {
            return tids;
        }

        public String getRealDiscussNum() {
            return realDiscussNum;
        }

        public TopicInfoRetExtraBean getExtra() {
            return extra;
        }
    }

    public static class TopicInfoRetExtraBean {
        @SerializedName("head_pic")
        private String headPic;
        @SerializedName("share_title")
        private String shareTitle;
        @SerializedName("share_pic")
        private String sharePic;
        @SerializedName("topic_tid")
        private String topicTid;

        public String getHeadPic() {
            return headPic;
        }

        public String getShareTitle() {
            return shareTitle;
        }

        public String getSharePic() {
            return sharePic;
        }

        public String getTopicTid() {
            return topicTid;
        }
    }
}

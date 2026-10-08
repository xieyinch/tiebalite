package com.huanchengfly.tieba.post.api.retrofit.interceptors

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.huanchengfly.tieba.post.api.models.CommonResponse
import com.huanchengfly.tieba.post.api.retrofit.exception.TiebaApiException
import okhttp3.Interceptor
import okhttp3.Response

object FailureResponseInterceptor : Interceptor {
    private val gson = Gson()

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val body = response.body
        if (!response.isSuccessful || body == null || body.contentLength() == 0L) return response

        //获取字符集
        val contentType = body.contentType()
        val charset = if (contentType == null) {
            Charsets.UTF_8
        } else {
            contentType.charset(Charsets.UTF_8)!!
        }

        val responseText = body.source().buffer.clone().readString(charset)
        val jsonObject = try {
            gson.fromJson<CommonResponse>(responseText, CommonResponse::class.java)
        } catch (exception: Exception) {
            //如果返回内容解析失败, 说明它不是一个合法的 json
            //如果在拦截器抛出 MalformedJsonException 会导致 Retrofit 的异步请求一直卡着直到超时
            return response
        }

        if (jsonObject.errorCode != null && jsonObject.errorCode != 0) {
            val isHotTopicThread = response.request.url.encodedPath == "/mo/q/hotMessage/thread"
            if (!isHotTopicThread || !hasHotTopicThreads(responseText, jsonObject.errorMsg)) {
                throw TiebaApiException(jsonObject)
            }
        }
        return response
    }

    /**
     * Tieba may return no=1 together with a valid thread_list for this endpoint.
     * Treat that payload as success; an empty error message is also a valid empty
     * result for the same endpoint.
     */
    private fun hasHotTopicThreads(responseText: String, errorMessage: String?): Boolean {
        return try {
            val root = JsonParser().parse(responseText).asJsonObject
            val threadList = root.getAsJsonObject("data")?.get("thread_list")
            val hasThreads = threadList != null && threadList.isJsonArray && threadList.asJsonArray.size() > 0
            hasThreads || errorMessage.isNullOrBlank()
        } catch (exception: Exception) {
            false
        }
    }
}
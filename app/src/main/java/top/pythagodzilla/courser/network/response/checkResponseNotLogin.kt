package top.pythagodzilla.courser.network.response

import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import top.pythagodzilla.courser.network.exception.SessionExpiredException
import top.pythagodzilla.courser.network.exception.UnknownException

/**
 * 目前几个接口返回的东西很奇怪，但是目前做如此归纳：
 * 1. 所有response都有status以及sessionid字段
 * 2. 如果请求的接口需要登录，即session无效，则返回status为[-2],蛮傻逼的。并且有error字段，为List<String>。
 * 3. 如果请求的接口符合，则返回status为1，并且没有error字段，取而代之的是有信息的datas字段
 *
 * @return Result<String>,成功时返回"Success",失败返回Exception，异常类型有几种，自己判断是哪种。
 * @param response Response对象，直接传入response判断即可。
 * */
suspend fun checkResponseNotLogin(response: Response): Result<Response> {

    val text = response.body.string()
    val jsonObj = JSONObject(text)

    val error = jsonObj.optJSONArray("error")
    val statusCode = jsonObj.opt("status")
    val datas = jsonObj.opt("datas")

    // 首先判断成功的情况
    if (error == null && statusCode == 1 && datas != null) {
        return Result.success(response)
    }

    // 失败的情况，首先判断是否是未登录状态
    // 1. status解析出来要等于[-2]
    // 2. error字段存在，且内容为“请登录！”

    if (statusCode is JSONArray && error != null) {
        if (statusCode.optInt(0) == -2 && error.optString(0) == "请登录！") {
            return Result.failure(
                SessionExpiredException(
                    response.code,
                    error.optString(0),
                    statusCode.optInt(0)
                )
            )
        }
    }

    return Result.failure(
        UnknownException(
            response.code,
            "HTTP error occurred: $text"
        )
    )
}
package com.whm.githubapp.network

        import android.content.Context
        import com.whm.githubapp.datastore.UserSessionManager
        import kotlinx.coroutines.GlobalScope
        import kotlinx.coroutines.launch
        import okhttp3.*
        import org.json.JSONObject
        import java.io.IOException

        object OAuthManager {
            private const val CLIENT_ID = "Ov23ctcH0eRM3sjBTHcz"
            private const val CLIENT_SECRET = "f1dc110a16a14183dafeb8d108aae49f8eb39c4a"
            private const val REDIRECT_URI = "https://your-app.com/callback"

            fun exchangeCodeForToken(code: String, context: Context) {
                val client = OkHttpClient()
                val requestBody = FormBody.Builder()
                    .add("client_id", CLIENT_ID)
                    .add("client_secret", CLIENT_SECRET)
                    .add("code", code)
                    .add("redirect_uri", REDIRECT_URI)
                    .build()

                val request = Request.Builder()
                    .url("https://github.com/login/oauth/access_token")
                    .post(requestBody)
                    .addHeader("Accept", "application/json")
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.body?.string()?.let {
                            val json = JSONObject(it)
                            val token = json.getString("access_token")
                            GlobalScope.launch {
                                UserSessionManager(context).saveToken(token)
                            }
                        }
                    }
                })
            }
        }
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class Squirrel {

    enum class Method {
        GET,
        POST,
        PUT,
        DELETE,
        PATCH,
        OPTIONS
    }

    companion object {
        val method_map = hashMapOf(
            Method.GET to "GET",
            Method.POST to "POST",
            Method.PUT to "PUT",
            Method.DELETE to "DELETE",
            Method.PATCH to "PATCH",
            Method.OPTIONS to "OPTIONS"
        )
    }

    class Request (val headers: HashMap<String, String>? = null, val data: String? = null, val url: String, val method: Method = Method.GET)
    {
        fun perform(method: String? = null): Response
        {
            val result = Response(status_code = 200)
            var connection: HttpURLConnection? = null
            try {

                val link = URL(this.url)
                connection = link.openConnection() as HttpURLConnection

                connection.requestMethod = method ?: method_map[this.method]

                headers?.forEach { entry -> connection.setRequestProperty(entry.key, entry.value) }
                if (this.method == Method.POST && data != null) {
                    connection.setRequestProperty("Content-Length", data.length.toString())
                }
                connection.useCaches = false
                connection.doOutput = true
                val stream = DataOutputStream(connection.outputStream)
                if (data != null) {
                    stream.write(data.toByteArray())
                }
                stream.close()
                result.status_code = connection.responseCode
                result.cookies = __get_cookies(connection.headerFields["Set-Cookie"])
                result.headers = connection.headerFields.values
                try {
                    val istream = connection.inputStream
                    val bufferReader = BufferedReader(InputStreamReader(istream))
                    result.text = bufferReader.readText()
                }
                catch (_: Exception) {}
            }
            finally {
                connection?.disconnect()
            }


            return result
        }

        private fun __get_cookies(header_fields: List<String>?): HashMap<String, String> {
            val map = HashMap<String, String>()

            header_fields?.forEach {
                val result = it.split("=", limit = 1)
                try {
                    map[result[0]] = result[1]
                }
                catch (_: Exception) {}
            }
            return map
        }
    }

    data class Response (
        var text: String? = null,
        var headers: MutableCollection<List<String>>? = null,
        var cookies: HashMap<String, String>? = null,
        var status_code: Int,
    )
}

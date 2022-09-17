fun main() {
    val resp = Squirrel.Request(
        url = "https://i.instagram.com/api/v1/accounts/send_password_reset/",
        method = Squirrel.Method.POST,
        data = "_csrftoken=&username={username}&guid={guid}&device_id={guid}",
        headers = hashMapOf(
            "User-Agent" to "Instagram 113.0.0.39.122 Android (24/5.0; 515dpi; 1440x2416; 'huawei/google; Nexus 6P; angler; angler; en_US)",
            "Content-Type" to "application/x-www-form-urlencoded"
        )
   ).perform()
   println(resp.text)
}

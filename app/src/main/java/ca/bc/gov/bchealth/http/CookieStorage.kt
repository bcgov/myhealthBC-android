package ca.bc.gov.bchealth.http

import java.util.HashSet

class CookieStorage {

    private var _cookies: HashSet<String>? = HashSet()

    fun store(cookies: HashSet<String>?) {
        _cookies = cookies
    }

    fun getCookies(): HashSet<String>? {
        return _cookies
    }

    fun clear() {
        _cookies!!.clear()
    }
}

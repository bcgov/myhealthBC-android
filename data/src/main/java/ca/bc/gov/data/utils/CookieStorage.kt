package ca.bc.gov.data.utils

/**
 * @author Pinakin Kansara
 */
class CookieStorage {

    private var _cookies: HashSet<String>? = HashSet()

    fun store(cookies: HashSet<String>?) {
        _cookies = cookies
    }

    fun getCookies(): HashSet<String>? {
        return _cookies
    }

    fun clear() {
        _cookies?.clear()
    }
}

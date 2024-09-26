package utils


import java.util.prefs.Preferences

object KeyValueStorage {
    private val prefs: Preferences = Preferences.userNodeForPackage(KeyValueStorage::class.java)

    fun put(key: String, value: String) {
        prefs.put(key, value)
    }

    fun get(key: String, defaultValue: String = ""): String {
        return prefs.get(key, defaultValue)
    }

    fun remove(key: String) {
        prefs.remove(key)
    }

    fun clear() {
        prefs.clear()
    }
}

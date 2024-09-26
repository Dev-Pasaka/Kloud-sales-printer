package presentation.screens.auth.signInScreen

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import utils.KeyValueStorage

class SignInScreenViewModel() : ScreenModel {

    private val _businessId = mutableStateOf("")
    val stationId get() = _businessId

    private var _printerName = mutableStateOf("")

    fun businessId(value: String) {
        _businessId.value = value
    }

    private fun getStationId() {
        _businessId.value = KeyValueStorage.get(key = "stationId")
    }


    fun initialize() {
        screenModelScope.launch {
            KeyValueStorage.put(
                key = "stationId",
                value = _businessId.value
            )

        }
    }

    init {
        screenModelScope.launch {
            getStationId()
        }
    }

}
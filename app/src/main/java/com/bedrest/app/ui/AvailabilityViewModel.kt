package com.bedrest.app.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bedrest.app.data.model.ResultData
import com.bedrest.app.domain.AvailabilityUseCase
import com.bedrest.app.domain.model.Availability
import com.bedrest.app.utils.NetworkUtil.getResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AvailabilityViewModel @Inject constructor(
    private val availabilityUseCase: AvailabilityUseCase
) : ViewModel() {

    private val _availability = MutableLiveData<ResultData<List<Availability>>>()
    val availability: LiveData<ResultData<List<Availability>>>
        get() = _availability

    private val hospitalList = ArrayList<Availability>()

    fun getAvailability(
        province: String
    ) {
        _availability.value = ResultData.Loading
        viewModelScope.launch(Dispatchers.IO) {
            hospitalList.clear()
            getResponse(
                availabilityUseCase.getHospitalAvailability(province),
                _availability,
                doOnSuccess = {
                    hospitalList.addAll(it)
                }
            )
        }
    }

    fun findHospital(hospitalCode: String?): Availability? {
        if (hospitalCode.isNullOrEmpty()) return null
        return hospitalList.find { it.hospital_code == hospitalCode }
    }
}
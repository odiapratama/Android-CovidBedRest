package com.bedrest.app.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bedrest.app.data.model.Availability
import com.bedrest.app.data.model.ResultData
import com.bedrest.app.data.repository.AvailabilityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AvailabilityViewModel @Inject constructor(
    private val availabilityRepository: AvailabilityRepository
) : ViewModel() {

    private val _availability = MutableLiveData<ResultData<List<Availability>>>()
    val availability: LiveData<ResultData<List<Availability>>>
        get() = _availability

    val hospitalList = ArrayList<Availability>()

    fun getAvailability(
        province: String,
        revalidate: Boolean = false
    ) {
        _availability.value = ResultData.Loading
        viewModelScope.launch {
            availabilityRepository.getHospitalAvailability(province, revalidate).let {
                _availability.postValue(it)
                hospitalList.clear()

                if (it is ResultData.Success) {
                    hospitalList.addAll(it.data)
                }
            }
        }
    }

    fun findHospital(hospitalCode: String?): Availability? {
        if (hospitalCode.isNullOrEmpty()) return null
        return hospitalList.find { it.hospital_code == hospitalCode }
    }
}
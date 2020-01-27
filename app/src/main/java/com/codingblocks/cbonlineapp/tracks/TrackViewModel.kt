package com.codingblocks.cbonlineapp.tracks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.util.STUDENT
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.models.CareerTracks
import com.codingblocks.onlineapi.models.Course
import com.codingblocks.onlineapi.models.Professions

/**
 * @author aggarwalpulkit596
 */
class TrackViewModel(private val repo: TracksRepository) : ViewModel() {

    var type: MutableLiveData<String> = MutableLiveData(STUDENT)
    lateinit var id: String
    var errorLiveData: MutableLiveData<String> = MutableLiveData()
    var currentTrack = MutableLiveData<CareerTracks>()
    var courses = MutableLiveData<List<Course>>()

    fun fetchCurrentTrack() {
        runIO {
            when (val response = repo.getTrack(id)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful) {
                        currentTrack.postValue(body())
                        body()?.run {
                            fetchTrackCourses(coursesLinks?.related?.href?.substring(8) ?: "")
                        }
                    } else {
                        setError(fetchError(code()))
                    }
                }
            }
        }
    }

    fun fetchTracks(): MutableLiveData<List<CareerTracks>> {
        val tracks = MutableLiveData<List<CareerTracks>>()
        runIO {
            when (val response = repo.getTracks()) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful) {
                        tracks.postValue(body())
                    } else {
                        setError(fetchError(code()))
                    }
                }
            }
        }
        return tracks
    }

    private fun fetchTrackCourses(id: String) {
        runIO {
            when (val response = repo.getTrackCourses(id)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful) {
                        courses.postValue(body())
                    } else {
                        setError(fetchError(code()))
                    }
                }
            }
        }
    }

    fun fetchProfessions(): MutableLiveData<List<Professions>> {
        val professions = MutableLiveData<List<Professions>>()

        runIO {
            when (val response = repo.getProfessions()) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful) {
                        professions.postValue(body())
                    } else {
                        setError(fetchError(code()))
                    }
                }
            }
        }
        return professions
    }

    private fun setError(error: String) {
        errorLiveData.postValue(error)
    }

    fun getRecommendedTrack(id: String): LiveData<CareerTracks> {
        val track = MutableLiveData<CareerTracks>()

        runIO {
            when (val response = repo.getRecommendedTrack(hashMapOf("professionId" to id, "status" to type.value!!))) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful) {
                        track.postValue(body())
                    } else {
                        setError(fetchError(code()))
                    }
                }
            }
        }
        return track
    }
}

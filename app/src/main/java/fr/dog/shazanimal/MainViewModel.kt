package fr.dog.shazanimal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.dog.shazanimal.analysis.Analysis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.support.audio.TensorAudio
import org.tensorflow.lite.task.audio.classifier.AudioClassifier

class MainViewModel : ViewModel() {
    private val _analysis = mutableListOf<Analysis>()
    val analysis: List<Analysis>
        get() = _analysis

    fun nextAnimal() = AvailableAnimal
        .values()
        .first {
            !_analysis.map(Analysis::animal).contains(it)
        }

    fun hasOneAnalysisLeft() = !_analysis.map(Analysis::animal).containsAll(AvailableAnimal.values().toList())

    suspend fun addAnalysis(
        animal: AvailableAnimal,
        classifier: AudioClassifier,
        tensorAudio: TensorAudio
    ) = withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
        Analysis
            .createFrom(animal, classifier, tensorAudio)
            .let(_analysis::add)
        _analysis.sortByDescending { it.score }
    }
}
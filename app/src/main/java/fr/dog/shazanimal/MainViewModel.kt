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

    fun nextAnimal() = analyzableAnimals().first { it.hasNotBeenAnalyzed() }

    fun hasAtLeastOneAnalysisLeft() = analyzableAnimals().size != analysis.size

    suspend fun addAnalysis(
        animal: AnalyzableAnimal,
        classifier: AudioClassifier,
        tensorAudio: TensorAudio
    ) = withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
        storeNewAnalysis(animal, classifier, tensorAudio)
        sortAnalysisByScoreDescending()
    }

    private fun sortAnalysisByScoreDescending() {
        _analysis.sortByDescending { it.score }
    }

    private suspend fun storeNewAnalysis(
        animal: AnalyzableAnimal,
        classifier: AudioClassifier,
        tensorAudio: TensorAudio
    ) {
        Analysis
            .createFrom(animal, classifier, tensorAudio)
            .let(_analysis::add)
    }

    private fun analyzableAnimals() = AnalyzableAnimal
        .values()
    private fun AnalyzableAnimal.hasNotBeenAnalyzed() = this !in alreadyAnalyzedAnimals()
    private fun alreadyAnalyzedAnimals() = _analysis.map(Analysis::animal)
}
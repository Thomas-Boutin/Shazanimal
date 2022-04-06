package fr.dog.shazanimal.analysis

import fr.dog.shazanimal.AnalyzableAnimal
import org.tensorflow.lite.support.audio.TensorAudio
import org.tensorflow.lite.task.audio.classifier.AudioClassifier

class Analysis private constructor(val animal: AnalyzableAnimal, val score: Float) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Analysis

        if (animal != other.animal) return false
        if (score != other.score) return false

        return true
    }

    override fun hashCode(): Int {
        var result = animal.hashCode()
        result = 31 * result + score.hashCode()
        return result
    }

    companion object {
        fun createFrom(
            animal: AnalyzableAnimal,
            classifier: AudioClassifier,
            tensorAudio: TensorAudio
        ): Analysis {
            return Analysis(
                animal,
                classifier
                    .classify(tensorAudio)
                    .flatMap { it.categories }
                    .first { it.index == animal.modelIndex }
                    .score * 100
            )
        }
    }
}
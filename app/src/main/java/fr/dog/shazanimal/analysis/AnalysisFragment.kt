package fr.dog.shazanimal.analysis

import android.media.AudioRecord
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import fr.dog.shazanimal.MainViewModel
import fr.dog.shazanimal.R
import fr.dog.shazanimal.databinding.FragmentAnalysisBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.support.audio.TensorAudio
import org.tensorflow.lite.task.audio.classifier.AudioClassifier

class AnalysisFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private val nextAnimal by lazy { viewModel.nextAnimal() }
    private val classifier: AudioClassifier by lazy {
        AudioClassifier.createFromFile(requireContext(), MODEL_FILE)
    }
    private val audioTensor: TensorAudio by lazy { classifier.createInputTensorAudio() }
    private val record: AudioRecord by lazy { classifier.createAudioRecord() }

    private var isRecording = false

    private lateinit var binding: FragmentAnalysisBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentAnalysisBinding.inflate(inflater, container, false)
            .also { binding = it }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            animalName.text = nextAnimal.name
            animal.text = nextAnimal.emoji
            analyze.setOnClickListener {
                switchRecordingState()
            }
        }
    }

    private fun switchRecordingState() = lifecycleScope.launch {
        if (isRecording) {
            stopRecordingAndAnalyse()
            analyzeNextAnimalOrShowResults()
        }
        if (!isRecording) {
            switchAnalyzeButtonToAnalysisMode()
            recordAudio()
            isRecording = true
        }
    }

    private fun analyzeNextAnimalOrShowResults() {
        if (viewModel.hasAtLeastOneAnalysisLeft()) {
            goTo(AnalysisFragmentDirections.actionAnalysisFragmentSelf())
        } else {
            goTo(AnalysisFragmentDirections.actionAnalysisFragmentToResultFragment())
        }
    }

    private suspend fun stopRecordingAndAnalyse() = withContext(Dispatchers.IO) {
        record.stop()
        audioTensor.load(record)
        viewModel.addAnalysis(nextAnimal, classifier, audioTensor)
    }

    private suspend fun recordAudio() = withContext(Dispatchers.IO) {
        record.startRecording()
    }

    private fun switchAnalyzeButtonToAnalysisMode()  = binding.analyze.apply {
        text = getString(R.string.stop_recording)
        setBackgroundColor(
            resources.getColor(
                R.color.electric_green,
                requireActivity().theme
            )
        )
        setTextColor(resources.getColor(R.color.black, requireActivity().theme))
        setIconTintResource(R.color.black)
    }

    private fun goTo(navDirections: NavDirections) = lifecycleScope.launch(Dispatchers.Main) {
        findNavController().navigate(navDirections)
    }

    companion object {
        private const val MODEL_FILE = "yamnet.tflite"
    }
}
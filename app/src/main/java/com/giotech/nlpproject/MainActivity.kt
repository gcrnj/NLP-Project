package com.giotech.nlpproject

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.giotech.nlpproject.databinding.ActivityMainBinding
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity() {

    companion object {
        const val EXTERNAL_PERMISSION_CODE = 123
    }

    val viewBinding by lifecycleAwareLazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    val nlpService by lifecycleAwareLazy {
        NLPService()
    }

    var selectedPdf: File? = null


    private val pickPdfLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            // You now have the Uri of the PDF
            selectedPdf = uri.toFile(this)
            viewBinding.selectedPdfTextView.apply {
                visible()
                text = selectedPdf?.name ?: ""
            }
            // Pass inputStream or uri to your NLP API
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(viewBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }

        initializeListeners()
    }

    private suspend fun getSummarizedResponse(): NLPResponse? = with(viewBinding) {
        delay(1000)
        return if (pdfRadioButton.isChecked) {
            selectedPdf?.let {
                nlpService.summarizeFromPdf(it)
            } ?: run {
                pdfErrorTextView.visible()
                pdfErrorTextView.text = getString(R.string.no_pdf_selected_error_text)
                null
            }
        } else {
            val inputText = inputEditText.text.toString().trim()
            if (inputText.isNotBlank()) {
                nlpService.summarizeFromText(inputText)
            } else {
                inputErrorTextView.visible()
                inputErrorTextView.text = getString(R.string.empty_input_error_text)
                inputErrorTextView.typewriterText(
                    "${getString(R.string.empty_input_error_text)} ${
                        getString(
                            R.string.empty_input_error_text
                        )
                    } ${getString(R.string.empty_input_error_text)} ${getString(R.string.empty_input_error_text)} ${
                        getString(
                            R.string.empty_input_error_text
                        )
                    } ${getString(R.string.empty_input_error_text)}"
                )

                null
            }
        }
    }

    private fun initializeListeners() = with(viewBinding) {
        inputTypeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.pdfRadioButton -> {
                    pdfSelectorLinearLayout.visible()
                    inputLinearLayout.gone()
                    // summarizeButton.isEnabled = selectedPdf != null
                }

                R.id.textRadioButton -> {
                    val text = inputEditText.text
                    pdfSelectorLinearLayout.gone()
                    inputLinearLayout.visible()
                    // summarizeButton.isEnabled = text != null &&  text.isNotBlank()
                }
            }
        }
        /**
         *
        inputEditText.doOnTextChanged { _, _, _, _ ->
        val inputText = inputEditText.text.toString().trim()
        summarizeButton.isEnabled = inputText.isNotBlank()
        }
         */
        pdfSelectorLinearLayout.setOnClickListener {
            pickPdfLauncher.launch(arrayOf("application/pdf"))
        }
        summarizeButton.setOnClickListener {
            it.isEnabled = false
            pdfErrorTextView.gone()
            inputErrorTextView.gone()
            outputTextView.gone()
            summarizeErrorTextView.gone()
            loadingIndicator.visible()

            lifecycleScope.launch {
                delay(1000)
                getSummarizedResponse()?.let {
                    if (!it.error.isNullOrBlank()) {
                        outputTextView.visible()
                        outputTextView.text = it.summarized
                    } else {
                        summarizeErrorTextView.visible()
                        summarizeErrorTextView.text = it.error ?: "Unknown error"
                        Toast.makeText(
                            this@MainActivity,
                            summarizeErrorTextView.text,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                loadingIndicator.gone()
                it.isEnabled = true
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (view is TextInputEditText) {
                val outRect = Rect()
                view.getGlobalVisibleRect(outRect)

                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    view.clearFocus()

                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }


}
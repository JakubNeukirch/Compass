package eu.jakubneukirch.compass.screen.main

import android.Manifest
import android.os.Bundle
import androidx.lifecycle.Observer
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import eu.jakubneukirch.compass.R
import eu.jakubneukirch.compass.base.BaseActivity
import eu.jakubneukirch.compass.utils.OnTextChangedListener
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<MainViewModel, MainState>() {

    override val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel.listenNorthDirectionChanges()
        observeErrors()
        requestLocationPermission()
    }

    private fun observeErrors() {
        viewModel.error.observe(this, Observer { event ->
            event?.pendingContent?.let { error ->
                val errorMessageId = when (error) {
                    MainViewModel.MainError.PROVIDERS_UNAVAILABLE -> R.string.providers_error
                    MainViewModel.MainError.UNKNOWN -> R.string.unknown_error
                }
                showMessage(getString(errorMessageId))
            }
        })
    }

    private fun requestLocationPermission() {
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    enableLocationFeatures()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                }
            })
            .check()
    }

    private fun enableLocationFeatures() {
        latitudeEditText.isEnabled = true
        longitudeEditText.isEnabled = true
        val onTextChangedListener = OnTextChangedListener {
            updateCoordinatesData()
        }
        latitudeEditText.addTextChangedListener(onTextChangedListener)
        longitudeEditText.addTextChangedListener(onTextChangedListener)
    }

    private fun updateCoordinatesData() {
        viewModel.setCoordinates(
            longitudeEditText.text.toString().toDoubleOrNull(),
            latitudeEditText.text.toString().toDoubleOrNull()
        )
    }

    override fun onStateChanged(state: MainState) {
        setRotation(state.degrees)
        setCordsSwitchState(state is MainState.CordsDirectionState)
    }

    private fun setCordsSwitchState(isCordsState: Boolean) {
        cordsSwitch.isChecked = isCordsState
    }

    private fun setRotation(rotation: Float) {
        compassArrow.rotation = rotation
    }
}

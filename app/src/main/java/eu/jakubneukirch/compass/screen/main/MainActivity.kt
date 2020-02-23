package eu.jakubneukirch.compass.screen.main

import android.Manifest
import android.os.Bundle
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import eu.jakubneukirch.compass.R
import eu.jakubneukirch.compass.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<MainViewModel, MainState>() {

    override val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel.listenDirectionChanges()
        requestLocationPermission()
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
    }

    private fun enableLocationFeatures() {
        latitudeEditText.isEnabled = true
        longitudeEditText.isEnabled = true
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

package eu.jakubneukirch.compass.screen.main

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Surface
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

    companion object {
        private const val NEEDED_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION
    }

    override val viewModel: MainViewModel by viewModel()
    private val _permissionExplanationDialog: AlertDialog by lazy {
        createPermissionExplanationDialog()
    }
    private val _locationExplanationDialog: AlertDialog by lazy {
        createLocationExplanationDialog()
    }
    private var _isLocationExplanationCanceled: Boolean = false

    private val _onCoordinationTextChangedListener = OnTextChangedListener {
        updateCoordinatesData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel.listenNorthDirectionChanges()
        observeErrors()
        requestLocationPermission()
    }

    override fun onResume() {
        super.onResume()
        enableLocationFeatures(
            Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                    || checkSelfPermission(NEEDED_PERMISSION) == PackageManager.PERMISSION_GRANTED
        )
    }

    private fun observeErrors() {
        viewModel.error.observe(this, Observer { event ->
            event?.pendingContent?.let { error ->
                when (error) {
                    MainViewModel.MainError.PROVIDERS_UNAVAILABLE -> showLocationExplanationDialog()
                    MainViewModel.MainError.LOCATION_PERMISSION_NOT_GRANTED -> requestLocationPermission()
                    MainViewModel.MainError.UNKNOWN -> showMessage(getString(R.string.unknown_error))
                }
            }
        })
    }

    private fun requestLocationPermission() {
        Dexter.withActivity(this)
            .withPermission(NEEDED_PERMISSION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    enableLocationFeatures(true)
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    if (response?.isPermanentlyDenied == true) {
                        showPermissionExplanationDialog()
                    } else {
                        requestLocationPermission()
                    }
                }
            })
            .check()
    }

    private fun enableLocationFeatures(isEnabled: Boolean) {
        latitudeEditText.isEnabled = isEnabled
        longitudeEditText.isEnabled = isEnabled
        if (isEnabled) {
            latitudeEditText.addTextChangedListener(_onCoordinationTextChangedListener)
            longitudeEditText.addTextChangedListener(_onCoordinationTextChangedListener)
            updateCoordinatesData()
        } else {
            latitudeEditText.removeTextChangedListener(_onCoordinationTextChangedListener)
            longitudeEditText.removeTextChangedListener(_onCoordinationTextChangedListener)
            requestLocationPermission()
        }
    }

    private fun updateCoordinatesData() {
        viewModel.setCoordinates(
            longitudeEditText.text.toString().toDoubleOrNull(),
            latitudeEditText.text.toString().toDoubleOrNull()
        )
    }

    override fun onStateChanged(state: MainState) {
        setRotation(state.degrees)
        setPointingState(state is MainState.CordsDirectionState)
    }

    private fun setPointingState(isCordsState: Boolean) {
        cordsSwitch.isChecked = isCordsState
        cordsLabel.text = getString(
            if (isCordsState)
                R.string.coordination_direction_on
            else
                R.string.coordination_direction_off
        )
    }

    private fun setRotation(rotation: Float) {
        val angle = getOrientationAngle()
        compassArrow.rotation = rotation - angle
    }

    private fun getOrientationAngle(): Int {
        return when (windowManager.defaultDisplay.rotation) {
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> 0
        }
    }

    private fun showPermissionExplanationDialog() {
        if (!_permissionExplanationDialog.isShowing) {
            _permissionExplanationDialog.show()
        }
    }

    private fun createPermissionExplanationDialog(): AlertDialog {
        return AlertDialog.Builder(this)
            .setTitle(R.string.permission_explanation_title)
            .setMessage(R.string.permission_explanation_description)
            .setPositiveButton(R.string.open) { dialog, _ ->
                dialog?.dismiss()
                openAppSettings()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog?.dismiss()
            }
            .create()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun showLocationExplanationDialog() {
        if (!_isLocationExplanationCanceled && !_locationExplanationDialog.isShowing) {
            _locationExplanationDialog.show()
        }
    }

    private fun createLocationExplanationDialog(): AlertDialog {
        return AlertDialog.Builder(this)
            .setTitle(R.string.location_explanation_title)
            .setMessage(R.string.location_explanation_description)
            .setPositiveButton(R.string.open) { dialog, _ ->
                dialog?.dismiss()
                openLocationSettings()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog?.dismiss()
                _isLocationExplanationCanceled = true
            }
            .create()
    }

    private fun openLocationSettings() {
        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }
}

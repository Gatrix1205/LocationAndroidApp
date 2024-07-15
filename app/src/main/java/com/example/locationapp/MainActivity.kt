package com.example.locationapp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.locationapp.ui.theme.LocationAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val locationViewModel : LocationViewModel = viewModel()
            LocationAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                   MyApp(locationViewModel)
                }
            }
        }
    }
}

@Composable
fun MyApp(locationViewModel: LocationViewModel
){
    val context = LocalContext.current
    val locationUtility = LocationUtility(context)
    LocationApp(locationUtility = locationUtility, context =  context, locationViewModel = locationViewModel)
}



@SuppressLint("LaunchDuringComposition")
@Composable
fun LocationApp(
    locationViewModel: LocationViewModel,
    locationUtility: LocationUtility,
    context: Context
){
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if(permissions[android.Manifest.permission.ACCESS_FINE_LOCATION]==true && permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION]==true){
                locationUtility.requestLocationUpdates(viewModel = locationViewModel)
            }else{
                val rationalReq = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    context,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)

                if(rationalReq){
                    Toast.makeText(context, "Location Permission is Required", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(context, "Location Permission is Required, Please turn it on it Settings", Toast.LENGTH_LONG).show()
                }
            }

        } )

    val location = locationViewModel.location.value
    val address = location?.let{
        locationUtility.reverseGeocodeLocation(location, context)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        if(location != null){
            Text("Address: ${location.latitude} ${location.longitude} \n $address  ")
        }else{
            Text(text = "Location not available")
        }

        Button(onClick = {
            if(locationUtility.hasLocationPermission(context)){
                locationUtility.requestLocationUpdates(locationViewModel)
            }else{
                requestPermissionLauncher.launch(
                    arrayOf(
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                    )
                )
            }
        }) {
            Text("Get Location")
        }

    }
}

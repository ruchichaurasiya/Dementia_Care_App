package com.example.dementia_app

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.telephony.SmsManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.dementia_app.ui.theme.Dementia_AppTheme
import com.google.android.gms.location.*
import kotlin.math.*

class AlarmActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            Dementia_AppTheme {
                AlarmScreen(fusedLocationClient)
            }
        }
    }
}

@Composable
fun AlarmScreen(fusedLocationClient: FusedLocationProviderClient) {
    val context = LocalContext.current
    val activity = context as Activity

    var liveLat by remember { mutableStateOf<Double?>(null) }
    var liveLon by remember { mutableStateOf<Double?>(null) }
    var homeLat by remember { mutableStateOf<Double?>(null) }
    var homeLon by remember { mutableStateOf<Double?>(null) }
    var safeRadius by remember { mutableStateOf("100") }
    var caregiverPhoneNumber by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("Waiting for location...") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA)) // Beige background
    )

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasSmsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestSmsPermission() {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.SEND_SMS),
            SMS_REQUEST_CODE
        )
    }



    fun sendAlertSms(phoneNumber: String, latitude: Double, longitude: Double) {
        if (!hasSmsPermission()) {
            requestSmsPermission()
            Toast.makeText(context, "SMS permission required", Toast.LENGTH_SHORT).show()
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:${context.packageName}")
            context.startActivity(intent)

            return
        }

        if (phoneNumber.isNotEmpty()) {
            val smsManager = SmsManager.getDefault()
            val message = "Alert! The person is outside the safe zone.\n" +
                    "Live Location: https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"


            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Toast.makeText(context, "Alert SMS sent to $phoneNumber", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
        }
    }


    fun getLiveLocation() {
        if (hasLocationPermission()) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    liveLat = it.latitude
                    liveLon = it.longitude
                    message = "Live Location: ($liveLon, $liveLat)"
                    homeLat?.let { hLat ->
                        homeLon?.let { hLon ->
                            val dist =
                                calculateDistanceMeters(hLat, hLon, it.latitude, it.longitude)
                            if (safeRadius.toDoubleOrNull() != null && dist > safeRadius.toDouble()) {
                                Toast.makeText(
                                    context,
                                    "Alert! Out of safe zone.",
                                    Toast.LENGTH_LONG
                                ).show()
                                sendAlertSms(caregiverPhoneNumber, it.latitude, it.longitude)
                            } else {
                                Toast.makeText(context, "Within safe zone", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                } ?: run {
                    message = "Failed to fetch location"
                }
            }.addOnFailureListener {
                message = "Failed: ${it.localizedMessage}"
            }
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
        }
    }




    fun openMapsToPickHomeLocation() {
        val uri = Uri.parse("geo:0,0?q=Select+home+location")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
            Toast.makeText(
                context,
                "Long press on a location in Maps to copy coordinates",
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(context, "Google Maps not found", Toast.LENGTH_SHORT).show()
        }
    }

    // Update location every 30 seconds
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(30000) // Delay for 30 seconds
            getLiveLocation()
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Safe Zone Monitor", style = MaterialTheme.typography.headlineSmall)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            OutlinedTextField(
                value = homeLat?.toString() ?: "",
                onValueChange = { homeLat = it.toDoubleOrNull() },
                label = { Text("Home Latitude (y)") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            )
            OutlinedTextField(
                value = homeLon?.toString() ?: "",
                onValueChange = { homeLon = it.toDoubleOrNull() },
                label = { Text("Home Longitude (x)") },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )
        }

        OutlinedTextField(
            value = safeRadius,
            onValueChange = { safeRadius = it },
            label = { Text("Safe Radius (meters)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = caregiverPhoneNumber,
            onValueChange = { caregiverPhoneNumber = it },
            label = { Text("Caregiver Phone Number") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Button(
            onClick = { openMapsToPickHomeLocation() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Set Home Address (Open Maps)")
        }

        Button(
            onClick = { getLiveLocation() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Get Live Location")
        }

        Text(
            text = "Live Coordinates: (${liveLon ?: "--"}, ${liveLat ?: "--"})",
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = message,
            modifier = Modifier.padding(top = 8.dp)
        )
    }

}




// Haversine formula to calculate distance in meters
fun calculateDistanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val R = 6371e3
    val φ1 = Math.toRadians(lat1)
    val φ2 = Math.toRadians(lat2)
    val Δφ = Math.toRadians(lat2 - lat1)
    val Δλ = Math.toRadians(lon2 - lon1)

    val a = sin(Δφ / 2).pow(2.0) + cos(φ1) * cos(φ2) * sin(Δλ / 2).pow(2.0)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return R * c
}

const val LOCATION_REQUEST_CODE = 1001
const val SMS_REQUEST_CODE = 1002



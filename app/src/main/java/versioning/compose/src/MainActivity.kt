package versioning.compose.src

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Label
import versioning.compose.src.ui.theme.LearningJetpackComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val database = FirebaseFirestore.getInstance()
            LearningJetpackComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), // Fill the maximum size available
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        Modifier.padding(16.dp),
                    ) {
//                        AuthScreen(onEnterClick = {
//                            Log.i("MainActivity", "onCreate: $it")
//                        })
                        LampadaOperations(database)
                        VentiladorOperations(database)
                    }
                }
            }
        }
    }
}

@Composable
fun LampadaOperations(database: FirebaseFirestore) {
    val docRef = database.collection("Lâmpada").document("1")
    var stateButton by remember { mutableStateOf(false)}

    DisposableEffect(Unit) {
        val listenerRegistration = docRef.addSnapshotListener { snapshot, error ->
            if(error != null){
                Log.e("FirestoreSnapshot", "Error listening to changes", error)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                stateButton = snapshot.getBoolean("Ligado") ?: false
            }
        }
        onDispose { listenerRegistration.remove() }
    }

    Row (
        Modifier
            .fillMaxWidth()
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(8.dp)
                .weight(6f),
            text = "Controle a Lâmpada",
        )
        Switch(checked = stateButton,
            onCheckedChange = { newState ->
                stateButton = newState
                docRef.update("Ligado", newState)
                    .addOnSuccessListener {
                        Log.d("FirestoreUpdate", "Switch successfully updated.")
                    }
                    .addOnFailureListener { e ->
                        Log.e("FirestoreUpdate", "Error updating switch.", e)
                    }
            })
    }
}

@Composable
fun VentiladorOperations(database: FirebaseFirestore) {
    val collectionRef = database.collection("Ventilador")

    Column(
        Modifier
            .fillMaxSize()
//            .padding(horizontal = 64.dp, vertical = 64.dp)
//            .padding(16.dp)
    ) {
        CustomSliderScreen(collectionRef)
        SwitchButtonVentilador(collectionRef)
    }
}

@Composable
fun SwitchButtonVentilador(collectionRef: CollectionReference) {
    val docRef = collectionRef.document("2")
    var stateButton by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val listenerRegistration = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("FirestoreSnapshot", "Error listening to changes", error)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                stateButton = snapshot.getBoolean("Ligado") ?: false
            }
        }

        onDispose { listenerRegistration.remove() }
    }

    Row(
        Modifier.fillMaxWidth()
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(8.dp)
                .weight(6f),
            text = "Controle o Ventilador",
        )
        Switch(
            checked = stateButton,
            onCheckedChange = { newState ->
                stateButton = newState
                docRef.update("Ligado", newState)
                    .addOnSuccessListener {
                        Log.d("FirestoreUpdate", "Switch successfully updated.")
                    }
                    .addOnFailureListener { e ->
                        Log.e("FirestoreUpdate", "Error updating switch.", e)
                    }
            }
        )
    }
}

@Composable
fun CustomSliderScreen(collectionRef: CollectionReference) {

    var sliderValue by remember { mutableStateOf(0f) }
    val docRef = collectionRef.document("2")

    LaunchedEffect(sliderValue) {
        val updates: Map<String, Any?> = hashMapOf(
            "Ligado" to false, // Assuming you want to set Ligado to false initially
            "Velocidade" to sliderValue.toInt()
        )
        // Update the document with the new values
        try {
            docRef.update(updates).addOnSuccessListener {
                Log.d("FirestoreUpdate", "Document successfully updated.")
            }.addOnFailureListener { e ->
                Log.e("FirestoreUpdate", "Error updating document.", e)
            }
        } catch (e: Exception) {
            Log.e("FirestoreUpdate", "Exception occurred.", e)
        }
    }
    Slider(
        modifier =
        Modifier.fillMaxWidth(),
//                .rotate(270f),
        value = sliderValue,
        onValueChange = {
            sliderValue = it
            Log.i("MainActivity", "Slider: $it")
        },
        valueRange = 0f..255f,
    )

}

@Preview
@Composable
fun SliderPreview() {
    LearningJetpackComposeTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
        }
    }
}
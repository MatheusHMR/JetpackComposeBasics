package versioning.compose.src

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
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
                        Modifier.padding(4.dp),
                    ) {
                        AuthScreen(onEnterClick = {
                            Log.i("MainActivity", "onCreate: $it")
                        })
                        CustomSliderScreen(database)
                    }
                }
            }
        }
    }
}


/*
* Os componentes tendem a ser stateless
* Precisamos configurar estados para coisas que queremos alterar valores com base em eventos
* */

@Composable
fun AuthScreen(
    onEnterClick: (User) -> Unit
) {
    Column {
        var username by remember {
            mutableStateOf("")
        }
        var password by remember {
            mutableStateOf("")
        }
        TextField(
            value = username,
            onValueChange = { newValue ->
                username = newValue
            },
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            label = {
                Text("Usuário")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Ícone que representa o usuário"
                )
            }
        )
        TextField(
            value = password,
            onValueChange = {
                password = it
            },
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            label = {
                Text("Senha")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Ícone de representação de senha em forma de reticências"
                )
            }
        )
        Button(
            onClick = {
                onEnterClick(
                    User(
                        username,
                        password
                    )
                )
            },
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            Text(text = "Entrar")
        }
    }
}

@Preview
@Composable
fun AuthScreenPreview() {
    LearningJetpackComposeTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AuthScreen(onEnterClick = {})
        }
    }
}

@Composable
fun CustomSliderScreen(database: FirebaseFirestore) {
    var sliderValue by remember {
        mutableStateOf(0f)
    }
    LaunchedEffect(sliderValue) {
        val updates: Map<String, Any?> = hashMapOf(
            "Ligado" to false, // Assuming you want to set Ligado to false initially
            "Velocidade" to sliderValue.toInt()
        )

        // Reference to the document
        val docRef = database.collection("Ventilador").document("2")

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
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Slider(
            modifier = Modifier.fillMaxWidth(),
            value = sliderValue,
            onValueChange = {
                sliderValue = it
                Log.i("MainActivity", "Slider: $it")
            },
            valueRange = 0f..255f,
        )
    }
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
            CustomSliderScreen(database = FirebaseFirestore.getInstance())
        }
    }
}
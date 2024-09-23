package fer.rom.mu
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

@Composable
@Preview
fun App() {
    MaterialTheme {
        Row(modifier = Modifier.fillMaxSize()) {
            // Sección izquierda: Opciones
            Column(
                modifier = Modifier
                    .width(200.dp)  // Ajusta el ancho según sea necesario
                    .fillMaxHeight()
                    .padding(8.dp)
            ) {
                Text("Opción 1")
                Text("Opción 2")
                Text("Opción 3")
                // Puedes agregar más opciones o un menú interactivo
            }

            // Sección derecha: Collage
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Text("Aquí se renderiza el collage")  // Esto lo cambiarás por tu lógica de renderización
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
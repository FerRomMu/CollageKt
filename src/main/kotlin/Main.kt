package fer.rom.mu
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

fun getImages(): List<String> {
    val assetsDir = File("assets/")
    return if (assetsDir.exists() && assetsDir.isDirectory) {
        assetsDir.listFiles { file -> file.extension == "png" }?.map { it.path } ?: emptyList()
    } else {
        emptyList()
    }
}

@Composable
fun loadImageFromAssets(filePath: String): ImageBitmap? {
    return try {
        val file = File(filePath)
        val bufferedImage = ImageIO.read(file)
        bufferedImage?.toComposeImageBitmap()
    } catch (e: IOException) {
        println("Error loading image: $filePath")
        null
    }
}

@Composable
@Preview
fun App() {
    val assetsPath = "assets/"  // Cambia esto según la ubicación de tus imágenes
    val blankImagePath = assetsPath + "blank.png"

    // Simula que en assets tienes varias imágenes
    val menuImages = getImages()

    val collageSize = 10 // 10x10 recuadros

    // Estado que almacena la imagen actualmente seleccionada
    var selectedImage by remember { mutableStateOf(menuImages.getOrNull(0) ?: blankImagePath) }

    // Estado para almacenar las imágenes actuales en el collage (inicialmente todas blank.png)
    val collageImages = remember { mutableStateListOf<String>().apply { repeat(collageSize * collageSize) { add(blankImagePath) } } }

    var gridSize by remember { mutableStateOf(IntSize.Zero) }

    MaterialTheme {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            // Sección izquierda: Imágenes del menú en dos columnas con borde
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .width(200.dp)
                    .fillMaxHeight()
                    .padding(8.dp)
                    .border(2.dp, Color.Gray)  // Borde alrededor de la sección
                    .padding(8.dp),
                contentPadding = PaddingValues(4.dp)
            ) {
                items(menuImages) { imagePath ->
                    val imageBitmap = loadImageFromAssets(imagePath)
                    imageBitmap?.let {
                        // Agregamos un borde para la imagen seleccionada
                        val isSelected = selectedImage == imagePath
                        Image(
                            bitmap = it,
                            contentDescription = null,
                            modifier = Modifier
                                .size(90.dp)  // Imágenes cuadradas
                                .border(3.dp, if (isSelected) Color.Red else Color.Transparent)  // Borde rojo si está seleccionada
                                .padding(4.dp)
                                .clickable {
                                    selectedImage = imagePath  // Cambia la imagen seleccionada al hacer click
                                }
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .border(2.dp, Color.Gray)  // Borde alrededor de la sección
                    .padding(8.dp)
                    .onGloballyPositioned { layoutCoordinates ->
                        // Obtener el tamaño del contenedor para calcular el tamaño dinámico de los recuadros
                        gridSize = layoutCoordinates.size
                    }
            ) {
                val recuadroSize = with(LocalDensity.current) {
                    // Calcular el tamaño dinámico para que cada recuadro sea cuadrado
                    (gridSize.width / collageSize).toDp()
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(collageSize),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    items(collageSize * collageSize) { index ->
                        val imageBitmap = loadImageFromAssets(collageImages[index])
                        imageBitmap?.let {
                            Image(
                                bitmap = it,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(recuadroSize)  // Tamaño dinámico calculado para cada recuadro
                                    .border(1.dp, Color.Black)  // Borde negro alrededor de cada imagen
                                    .clickable {
                                        collageImages[index] = selectedImage  // Cambia la imagen en el recuadro clickeado
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
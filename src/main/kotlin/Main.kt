package fer.rom.mu
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

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

fun getImages(): List<String> {
    val assetsDir = File("assets/")
    return if (assetsDir.exists() && assetsDir.isDirectory) {
        assetsDir.listFiles { file -> file.extension == "png" }?.map { it.path } ?: emptyList()
    } else {
        emptyList()
    }
}

@Composable
fun App() {
    val assetsPath = "assets/"  // Cambia esto según la ubicación de tus imágenes
    val blankImagePath = assetsPath + "blank.png"

    // Lista de imágenes del menú obtenida automáticamente de la carpeta assets
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
            Column(modifier = Modifier
                .width(200.dp)
                .fillMaxHeight()
                .padding(8.dp)
                .border(2.dp, Color.Gray)  // Borde alrededor de la sección
                .padding(8.dp)) {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
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

                // Botón "Guardar"
                Button(
                    onClick = {
                        saveCollageAsImageNonComposable(collageImages, collageSize, 40, "assets/collage_output.png")
                    },
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                ) {
                    Text("Guardar")
                }
            }

            // Sección derecha: Collage 10x10 de imágenes con click para cambiar
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

// Función no composable para cargar imágenes directamente
fun loadImageForSaving(imagePath: String): BufferedImage? {
    return try {
        val file = File(imagePath)
        ImageIO.read(file)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// Función para guardar el collage como imagen
fun saveCollageAsImageNonComposable(collageImages: List<String>, collageSize: Int, recuadroSize: Int, outputPath: String) {
    val collageWidth = collageSize * recuadroSize
    val collageHeight = collageSize * recuadroSize

    // Crear una imagen en blanco con el tamaño del collage
    val collageImage = BufferedImage(collageWidth, collageHeight, BufferedImage.TYPE_INT_ARGB)

    val graphics = collageImage.createGraphics()

    // Dibujar cada imagen en su posición correspondiente
    for (row in 0 until collageSize) {
        for (col in 0 until collageSize) {
            val index = row * collageSize + col
            val imagePath = collageImages[index]

            val imageBitmap = loadImageForSaving(imagePath)
            imageBitmap?.let {
                graphics.drawImage(it, col * recuadroSize, row * recuadroSize, recuadroSize, recuadroSize, null)
            }
        }
    }

    // Finalizar el dibujo
    graphics.dispose()

    // Guardar la imagen en un archivo
    val outputFile = File(outputPath)
    ImageIO.write(collageImage, "png", outputFile)
    println("Collage guardado en: ${outputFile.absolutePath}")
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}

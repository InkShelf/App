import app.cash.zipline.ZiplineService
import kotlinx.serialization.Serializable

// commonMain
interface ComicSource : ZiplineService {
    suspend fun fetchComics(): List<Comic>
}

// A simple data class to represent a comic
@Serializable
data class Comic(val title: String, val url: String)
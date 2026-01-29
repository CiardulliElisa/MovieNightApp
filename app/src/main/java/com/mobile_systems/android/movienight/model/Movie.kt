import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Movie(
    val title: String? = null,
    val thumbnail: String? = null,
    val genres: List<String> = emptyList(),
    @SerialName("resource")
    val data: MovieResource? = null,
    val likes: Int = 0,
    val dislikes: Int = 0
)

@Serializable
data class MovieResource(
    @SerialName("imdb_id")
    val movieId: String? = null,
)
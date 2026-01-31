import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Movie(
    val thumbnail: String? = null,
    @SerialName("resource")
    val data: MovieResource = MovieResource(),
    val likes: Int = 0,
    val dislikes: Int = 0
)

@Serializable
data class MovieResource(
    @SerialName("imdb_id")
    val movieId: String? = "",
)
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieDetails(
    @SerialName("imdb_id")
    val movieId: String = "",
    val title: String = "",
    @SerialName("trailer")
    val content: TrailerInfo? = null,
)
@Serializable
data class TrailerInfo(
    @SerialName("thumbnail")
    val thumbnail: String = "",
    val genres: List<String> = emptyList()
)
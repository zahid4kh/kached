import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File

class Database {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private val appDir: File
    private val settingsFile: File
    private val snippetsFile: File

    init {
        val userHome = System.getProperty("user.home")
        appDir = File(userHome, ".kached").apply {
            if (!exists()) mkdirs()
        }

        settingsFile = File(appDir, "settings.json")
        snippetsFile = File(appDir, "snippets.json")

        if (!settingsFile.exists()) settingsFile.writeText(json.encodeToString(AppSettings()))
        if (!snippetsFile.exists()) {
            snippetsFile.writeText(json.encodeToString(emptyList<Snippet>()))
        }
    }

    suspend fun getSettings(): AppSettings = withContext(Dispatchers.IO) {
        return@withContext try {
            json.decodeFromString(settingsFile.readText())
        } catch (e: Exception) {
            AppSettings()
        }
    }

    suspend fun getSnippets(): List<Snippet> = withContext(Dispatchers.IO){
        return@withContext try {
            json.decodeFromString<List<Snippet>>(snippetsFile.readText())
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveSnippets(snippets: List<Snippet>) = withContext(Dispatchers.IO){
        val toSave = json.encodeToString(snippets)
        snippetsFile.writeText(toSave)
    }

    suspend fun saveSettings(settings: AppSettings) = withContext(Dispatchers.IO) {
        settingsFile.writeText(json.encodeToString(settings))
    }
}
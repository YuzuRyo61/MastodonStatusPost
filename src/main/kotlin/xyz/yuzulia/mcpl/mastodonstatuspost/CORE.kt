package xyz.yuzulia.mcpl.mastodonstatuspost

import com.google.gson.Gson
import com.sys1yagi.mastodon4j.MastodonClient
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class CORE : JavaPlugin() {

    private val log = getLogger()
    private val cfg = this.getConfig()
    private val df = SimpleDateFormat(cfg.getString("post.dateformat"))

    private var langf: File? = null
    private var startf: File? = null
    private var stopf: File? = null
    private var restartf: File? = null
    private var cpsoilerf: File? = null

    var start : String = ""
    var stop : String = ""
    var restart : String = ""
    var cpspoiler : String = ""

    private var lang: FileConfiguration? = null

    fun getLangConfig() : FileConfiguration? {
        return lang
    }

    fun createFiles() {
        this.langf = File(dataFolder, "lang/" + cfg.getString("language") + ".yml")
        this.startf = File(dataFolder, "template/start.txt")
        this.stopf = File(dataFolder, "template/stop.txt")
        this.restartf = File(dataFolder, "template/restart.txt")
        this.cpsoilerf = File(dataFolder, "template/cpspoiler.txt")

        if (!langf!!.exists()){
            langf!!.parentFile.mkdirs()
            saveResource("lang/" + cfg.getString("language") + ".yml", false)
            print("Created lang/" + cfg.getString("language") + ".yml")
        }

        if (!startf!!.exists()){
            startf!!.parentFile.mkdirs()
            saveResource("template/start.txt", false)
            print("Created template/start.txt")
        }

        if (!stopf!!.exists()){
            stopf!!.parentFile.mkdirs()
            saveResource("template/stop.txt", false)
            print("Created template/stop.txt")
        }

        if (!restartf!!.exists()){
            restartf!!.parentFile.mkdirs()
            saveResource("template/restart.txt", false)
            print("Created template/restart.txt")
        }

        if (!cpsoilerf!!.exists()){
            cpsoilerf!!.parentFile.mkdirs()
            saveResource("template/cpspoiler.txt", false)
            print("Created template/cpspoiler.txt")
        }

        lang = YamlConfiguration()

        try {
            lang!!.load(langf)

            val startlines = startf!!.readLines()
            start = ""
            startlines.forEach{ start += it + "\n" }

            val stoplines = stopf!!.readLines()
            stop = ""
            stoplines.forEach{ stop += it + "\n" }

            val restartlines = restartf!!.readLines()
            restart = ""
            restartlines.forEach{ restart += it + "\n" }

            val cpspoilerlines = cpsoilerf!!.readLines()
            cpspoiler = ""
            cpspoilerlines.forEach{ cpspoiler += it + "\n" }

        } catch (e: Exception) {
            log.info("EXCEPT: ")
            e.printStackTrace()
        }
    }

    fun plinit() =
            if (cfg.getString("application.address") == "notset" || cfg.getString("application.clientKey") == "notset" || cfg.getString("application.clientSecret") == "notset" || cfg.getString("application.accesstoken") == "notset") {
                log.info(lang!!.getString("info.notsetup"))
            } else {
                if(cfg.getString("post.start")!!.toBoolean()) {
                    try {
                        val client: MastodonClient = MastodonClient.Builder(cfg.getString("application.address"), OkHttpClient.Builder(), Gson())
                                .accessToken(cfg.getString("application.accesstoken"))
                                .build()

                        val date = Date()

                        val statusstart: String = cfg.getString("post.prefix") + "\n" + start + if(cfg.getString("post.includetime")!!.toBoolean()){ "\n" + df.format(date)}else{}

                        val bodystart: RequestBody = FormBody.Builder()
                                .add("status", statusstart)
                                .add("visibility", cfg.getString("post.visibility"))
                                .build()

                        client.post("statuses", bodystart)
                        log.info(lang!!.getString("info.postsuccess"))
                    } catch (e: Mastodon4jRequestException) {
                        e.printStackTrace()
                        log.info(lang!!.getString("error.keyerror"))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {}
            }


    override fun onEnable() {
        try {
            if (!File(dataFolder, "config.yml").exists()){
                this.saveDefaultConfig()
            }
            createFiles()

            getCommand("mstdnpost").executor = MstCMD(this)

            this.plinit()

            log.info("Enabled!")
        } catch (e: Exception){
            log.info("EXCEPT: ")
            e.printStackTrace()
        }
    }

    override fun onDisable() {
        saveConfig()
        if(cfg.getString("post.stop")!!.toBoolean()) {
            try {
                val date = Date()
                val statusstop: String = cfg.getString("post.prefix") + "\n" + stop + if(cfg.getString("post.includetime")!!.toBoolean()){ "\n" + df.format(date)}else{}

                val bodystop: RequestBody = FormBody.Builder()
                        .add("status", statusstop)
                        .add("visibility", cfg.getString("post.visibility"))
                        .build()

                val client: MastodonClient = MastodonClient.Builder(cfg.getString("application.address"), OkHttpClient.Builder(), Gson())
                        .accessToken(cfg.getString("application.accesstoken"))
                        .build()

                client.post("statuses", bodystop)
                log.info(lang!!.getString("info.postsuccess"))
            } catch (e: Mastodon4jRequestException) {
                e.printStackTrace()
                log.info(lang!!.getString("error.postfailed"))
            }
        } else {}
        log.info("Plugin will disabling...")
        log.info("Powered by YuzuRyo61.")
        log.info("Site: https://yuzulia.com/")
    }
}
package xyz.yuzulia.mcpl.mastodonstatuspost

import org.bukkit.command.defaults.ReloadCommand
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.*

class CORE : JavaPlugin() {

    val log = getLogger()
    val cfg = this.getConfig()

    private var langf: File? = null
    private var startf: File? = null
    private var stopf: File? = null
    private var restartf: File? = null

    private val langcfg: FileConfiguration? = this.getLangConfig()
    private var lang: FileConfiguration? = null

    fun getLangConfig() : FileConfiguration? {
        return lang
    }


    fun createFiles() {
        this.langf = File(dataFolder, "lang/" + cfg.getString("language") + ".yml")
        this.startf = File(dataFolder, "template/start.txt")
        this.stopf = File(dataFolder, "template/stop.txt")
        this.restartf = File(dataFolder, "template/restart.txt")

        if (!langf!!.exists()){
            langf!!.parentFile.mkdirs()
            saveResource("lang/" + cfg.getString("language") + ".yml", false)
            print("Created lang/" + cfg.getString("language") + ".yml")
        }

        if (!startf!!.exists()){
            langf!!.parentFile.mkdirs()
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

        lang = YamlConfiguration()

        try {
            lang!!.load(langf)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun plinit() {
        if (cfg.getString("application.address") == "notset" || cfg.getString("application.clientKey") == "notset" || cfg.getString("application.clientSecret") == "notsey" || cfg.getString("application.accesstoken") == "notset") {
            log.info(lang!!.getString("info.notsetup"))
        }
    }

    override fun onEnable() {
        try {
            this.saveDefaultConfig()
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
        cfg.options().copyDefaults(true)
        saveConfig()
        log.info("Powered by YuzuRyo61.")
        log.info("Site: https://yuzulia.com/")
    }
}
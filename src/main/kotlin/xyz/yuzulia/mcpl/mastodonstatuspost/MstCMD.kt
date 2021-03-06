package xyz.yuzulia.mcpl.mastodonstatuspost

import com.google.gson.Gson
import com.sys1yagi.mastodon4j.MastodonClient
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import java.util.logging.Logger
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import java.text.SimpleDateFormat
import java.util.*

open class MstCMD(val plg : CORE, val log : Logger = plg.getLogger()) : CommandExecutor {
    var lang: FileConfiguration = plg.getLangConfig()!!
    var cfg: FileConfiguration = plg.getConfig()
    private val df = SimpleDateFormat(cfg.getString("post.dateformat"))
    override fun onCommand(sender: CommandSender, cmd: Command, str: String, params: Array<out String>): Boolean {
        return when (cmd.getName().toLowerCase()) {
            "mstdnpost" -> {
                if (!params.isEmpty())
                    when (params[0].toLowerCase()) {
                        "post" -> {
                            if (sender.hasPermission("yuzulia.mstdnstatpost.post")) {
                                if (!params[1].isEmpty()) {
                                    try {

                                        val cmdsender : String?
                                        if (sender is Player){
                                            cmdsender = sender.name
                                        } else {
                                            cmdsender = "CONSOLE"
                                        }

                                        val date = Date()

                                        val client: MastodonClient = MastodonClient.Builder(cfg.getString("application.address"), OkHttpClient.Builder(), Gson())
                                                .accessToken(cfg.getString("application.accesstoken"))
                                                .build()

                                        val post: RequestBody = FormBody.Builder()
                                                .add("status", cfg.getString("post.prefix") + "\n" + params[1] + "\n" + if(cfg.getString("post.includetime")!!.toBoolean()){ "\n" + df.format(date)}else{})
                                                .add("visibility", cfg.getString("post.visibility"))
                                                .add("spoiler_text", plg.cpspoiler.replace("%sender%", cmdsender!!))
                                                .build()

                                        client.post("statuses", post)
                                        sender.sendMessage(lang.getString("info.postsuccess"))
                                    } catch(e: Mastodon4jRequestException) {
                                        e.printStackTrace()
                                        sender.sendMessage(lang.getString("error.failed"))
                                    }
                                    true
                                } else {
                                    sender.sendMessage(cfg.getString("usage.post"))
                                    true
                                }
                            } else {
                                sender.sendMessage(lang.getString("error.permission"))
                                true
                            }
                        }
                        "setup" -> {
                            if (sender.hasPermission("yuzulia.mstdnstatpost.setup")) {
                                if(!params[1].isEmpty()) {
                                    try {
                                        mstsetup(params[1], sender, cfg, lang)
                                    } catch(e: Exception) {
                                        e.printStackTrace()
                                        sender.sendMessage(lang.getString("error.unableconnect"))
                                    }
                                } else {
                                    sender.sendMessage(lang.getString("error.setupincorrect"))
                                }
                                true
                            } else {
                                sender.sendMessage(lang.getString("error.permission"))
                                true
                            }
                        }
                        "setupurl" -> {
                            if (sender.hasPermission("yuzulia.mstdnstatpost.setup")) {
                                sender.sendMessage("setupurl")
                                true
                            } else {
                                sender.sendMessage(lang.getString("error.permission"))
                                true
                            }
                        }
                        "auth" -> {
                            if (sender.hasPermission("yuzulia.mstdnstatpost.setup")) {
                                sender.sendMessage("auth")
                                true
                            } else {
                                sender.sendMessage(lang.getString("error.permission"))
                                true
                            }
                        }
                        "cred" -> {
                            if (sender.hasPermission("yuzulia.mstdnstatpost.credentials")) {
                                if (cfg.getString("application.address") == "notset" || cfg.getString("application.accesskey") == "notset") {

                                } else {
                                    sender.sendMessage(lang.getString("info.notsetup"))
                                }
                            } else {
                                sender.sendMessage(lang.getString("error.permission"))
                            }
                            true
                        }
                        "reload" -> {
                            if (sender.hasPermission("yuzulia.mstdnstatpost.reload")) {
                                sender.sendMessage(lang.getString("info.reloading"))
                                plg.saveConfig()
                                plg.reloadConfig()
                                plg.createFiles()
                                lang = plg.getLangConfig()!!
                                plg.plinit()
                                sender.sendMessage(lang.getString("info.reloaded"))
                                true
                            } else {
                                sender.sendMessage(lang.getString("error.permission"))
                                false
                            }
                        }
                        else -> false
                    }
                else
                    false
            }
            else -> false
        }
    }
}
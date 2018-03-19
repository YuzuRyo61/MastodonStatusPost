package xyz.yuzulia.mcpl.mastodonstatuspost

import java.util.logging.Logger
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration

open class MstCMD(val plg : CORE, val log : Logger = plg.getLogger()) : CommandExecutor {
    var lang: FileConfiguration = plg.getLangConfig()!!
    override fun onCommand(sender: CommandSender, cmd: Command, str: String, params: Array<out String>): Boolean {
        return when (cmd.getName().toLowerCase()) {
            "mstdnpost" -> {
                if (!params.isEmpty())
                    when (params[0].toLowerCase()) {
                        "post" -> {
                            if (sender.hasPermission("yuzulia.mstdnstatpost.post")) {
                                true
                            } else {
                                sender.sendMessage(lang.getString("error.permission"))
                                true
                            }
                        }
                        "setup" -> {
                            if (sender.hasPermission("yuzulia.mstdnstatpost.setup")) {
                                sender.sendMessage("Setup")
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
                        "forcesetup" -> {
                            if (sender.hasPermission("yuzulia.mstdnstatpost.setup")) {
                                sender.sendMessage("forcesetup")
                                true
                            } else {
                                sender.sendMessage(lang.getString("error.permission"))
                                true
                            }
                        }
                        "auth" -> {
                            if (sender.hasPermission("yuzulia.mstdnpost.setup")) {
                                sender.sendMessage("auth")
                                true
                            } else {
                                sender.sendMessage(lang.getString("error.permission"))
                                true
                            }
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
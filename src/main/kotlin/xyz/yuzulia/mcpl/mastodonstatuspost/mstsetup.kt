package xyz.yuzulia.mcpl.mastodonstatuspost

import com.google.gson.Gson
import com.sys1yagi.mastodon4j.MastodonClient
import okhttp3.OkHttpClient
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration

open class mstsetup(address: String, sender: CommandSender, cfg: FileConfiguration, lang: FileConfiguration) {
    val client: MastodonClient = MastodonClient.Builder(address, OkHttpClient.Builder(), Gson()).build()
}
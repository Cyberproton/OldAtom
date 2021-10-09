package me.cyberproton.atom.common.command.gui

import me.cyberproton.atom.api.command.Command
import me.cyberproton.atom.api.command.CommandArgument
import me.cyberproton.atom.api.gui.*
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer

class GuiTestCommand() : Command("gui test", "atom.gui.test", playerOnly = true)
{
    override fun execute(
        sender: CommandSender,
        args: List<CommandArgument>,
        stringArgs: Array<out String>,
        numberOfArgs: Int
    ) {
        Test(sender as Player)
    }

    class Test(val player: Player) : Gui("skills", 6, 9, "&b&lSkill") {
        private val view: View

        init {
            val item = ItemStack(Material.BLACK_STAINED_GLASS_PANE)
            val guiItem = setBaseItem(0, item)
            guiItem.clickHandler = Consumer { it.whoClicked.sendMessage("Bla bla") }

            view = createView(player, rows, cols)

            val item2 = ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE)
            val guiItem2 = setBaseItem(1, item2)
            setInputMask(setOf(1, 2))

            val layer = DefaultLayer("test", 6, 9, 3)
            addLayer(layer)
            val item3 = GuiItem(Material.PUFFERFISH)
            setItem("test", 3, item3)

            val item4 = GuiItem(Material.DIAMOND)
            setItem("base", 3, item4)
            setItem("base", 5, GuiItem(Material.DIAMOND))

            val il = InputOutputLayer("testinput", 6, 9, 4, (0..50).toSet(), MaskedLayer.Type.BLACK)
            addLayer(il)
        }
    }
}
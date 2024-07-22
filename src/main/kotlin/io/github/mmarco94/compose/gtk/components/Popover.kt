package io.github.mmarco94.compose.gtk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.mutableStateOf
import io.github.mmarco94.compose.GtkApplier
import io.github.mmarco94.compose.GtkComposeNode
import io.github.mmarco94.compose.SingleChildComposeNode
import io.github.mmarco94.compose.VirtualComposeNode
import io.github.mmarco94.compose.modifier.Modifier
import io.github.mmarco94.compose.modifier.combine
import io.github.mmarco94.compose.shared.ActionGroupBuilderScope
import io.github.mmarco94.compose.shared.MenuBuilderScope
import io.github.mmarco94.compose.shared.actionGroupBase
import io.github.mmarco94.compose.shared.menuBase
import org.gnome.gtk.Popover
import org.gnome.gtk.PopoverMenu
import org.gnome.gtk.Widget

/**
 * Scope to interact with Popover
 */
interface PopoverScope {
    fun Modifier.popoverTrigger(): Modifier
    var popover: Popover

    fun popup() {
        popover.popup()
    }

    fun popdown() {
        popover.popdown()
    }
}

@Composable
fun Popover(
    modifier: Modifier = Modifier,
    arrow: Boolean = true,
    trigger: @Composable PopoverScope.() -> Unit,
    content: @Composable () -> Unit,
) {
    val scope = object : PopoverScope {
        override var popover: Popover = Popover.builder()
            .setHasArrow(arrow)
            .build()

        val parent = mutableStateOf<Widget?>(null)

        override fun Modifier.popoverTrigger(): Modifier = combine{
            parent.value = it
            popover.parent = it
        }
    }
    scope.trigger()
    ComposeNode<GtkComposeNode<Popover>, GtkApplier>(
        factory = {
            println("Creating popover")
            SingleChildComposeNode<Popover>(
                scope.popover
            ) {
                child = it
            }
        },
        update = {
            set(modifier) { applyModifier(it) }
            set(arrow) { gObject.hasArrow = it }
            set(scope.parent) {
                if (it.value != null) {
                    gObject.parent = it.value
                }
            }
        },
        content = content
    )
}

/**
 * @see [menu]
 */
@Composable
fun PopoverMenu(arrow: Boolean = true, trigger: @Composable PopoverScope.() -> Unit, content: PopoverScope.() -> Unit) {
    val scope = object : PopoverScope {
        override var popover: Popover = PopoverMenu.builder()
            .setHasArrow(arrow)
            .build()

        override fun Modifier.popoverTrigger(): Modifier = combine {
            popover.parent = it
        }
    }
    scope.content()
    scope.trigger()
}

/**
 * @see [io.github.mmarco94.compose.shared.section]
 * @see [io.github.mmarco94.compose.shared.submenu]
 * @see [io.github.mmarco94.compose.shared.item]
 */
fun PopoverScope.menu(builder: MenuBuilderScope.() -> Unit) {
    (popover as? PopoverMenu)?.apply {
        menuModel = menuBase(builder)
    }
}

/**
 * @see [io.github.mmarco94.compose.shared.action]
 */
fun PopoverScope.actionGroup(name: String, builder: ActionGroupBuilderScope.() -> Unit) {
    popover.insertActionGroup(name, actionGroupBase(builder))
}

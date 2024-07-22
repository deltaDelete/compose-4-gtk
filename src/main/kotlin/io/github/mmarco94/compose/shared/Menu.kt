package io.github.mmarco94.compose.shared

import org.gnome.gio.Menu
import org.gnome.gio.MenuItem
import org.gnome.gio.MenuModel

interface MenuBuilderScope {
    val menuBuilder: Menu

    companion object {
        fun of(menu: Menu) = object : MenuBuilderScope {
            override val menuBuilder = menu
        }
    }
}

internal fun menuBase(builder: MenuBuilderScope.() -> Unit): MenuModel {
    val menu: Menu = Menu.builder().build()
    val scope = MenuBuilderScope.of(menu)
    scope.builder()
    return menu
}

fun menu(builder: MenuBuilderScope.() -> Unit): MenuModel = menuBase(builder)

fun MenuBuilderScope.section(label: String?, builder: MenuBuilderScope.() -> Unit) {
    val menu: Menu = Menu.builder().build()
    val scope = MenuBuilderScope.of(menu)
    scope.builder()
    this.menuBuilder.appendSection(label, menu)
}

fun MenuBuilderScope.submenu(label: String?, builder: MenuBuilderScope.() -> Unit) {
    val menu: Menu = Menu.builder().build()
    val scope = MenuBuilderScope.of(menu)
    scope.builder()
    this.menuBuilder.appendSubmenu(label, menu)
}

fun MenuBuilderScope.item(label: String?, action: String? = null) {
    val item = MenuItem(label, action)
    this.menuBuilder.appendItem(item)
}
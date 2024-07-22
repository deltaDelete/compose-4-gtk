import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.mmarco94.compose.gtk.components.ApplicationWindow
import io.github.mmarco94.compose.gtk.application
import io.github.mmarco94.compose.adw.components.HeaderBar
import io.github.mmarco94.compose.gtk.components.Button
import io.github.mmarco94.compose.gtk.components.Label
import io.github.mmarco94.compose.gtk.components.Popover
import io.github.mmarco94.compose.gtk.components.menu
import io.github.mmarco94.compose.gtk.components.PopoverMenu
import io.github.mmarco94.compose.gtk.components.VerticalBox
import io.github.mmarco94.compose.gtk.components.actionGroup
import io.github.mmarco94.compose.modifier.Modifier
import io.github.mmarco94.compose.shared.action
import io.github.mmarco94.compose.shared.item
import io.github.mmarco94.compose.shared.menu
import io.github.mmarco94.compose.shared.section
import io.github.mmarco94.compose.shared.submenu
import org.gnome.gio.SimpleAction
import org.gnome.gio.SimpleActionGroup
import org.gnome.glib.Variant
import org.gnome.graphene.Rect
import org.gnome.gtk.Box
import org.gnome.gtk.Popover
import org.gnome.gtk.PopoverMenu
import org.gnome.gtk.Widget

fun main(args: Array<String>) {
    application("my.example.HelloApp", args) {
        ApplicationWindow(application, title = "Test", onClose = ::exitApplication) {
            VerticalBox(spacing = 10) {
                HeaderBar()

                var pop by remember { mutableStateOf<Popover?>(null) }
                Button("Popover as modifier", modifier = Modifier.of {
                    val box = Box.builder().build()
                    box.append(org.gnome.gtk.Label("Hello, world!"))
                    val popover = Popover.builder()
                        .setChild(box)
                        .build()
                    pop = popover
                    popover.parent = it
                }) {
                    pop?.popup()
                }

                var popMenu by remember { mutableStateOf<Popover?>(null) }
                var refMenu by remember { mutableStateOf<Widget?>(null) }
                Button("PopoverMenu as modifier", modifier = Modifier.of {
                    SimpleActionGroup.builder()
                        .build().apply {
                            addAction(
                                SimpleAction.builder()
                                    .setName("action1")
                                    .setEnabled(true)
                                    .build().apply {
                                        onActivate {
                                            println("Some")
                                        }
                                    }
                            )
                            addAction(
                                SimpleAction.builder()
                                    .setName("action2")
                                    .setState(Variant.boolean_(true))
                                    .setEnabled(true)
                                    .build().apply {
                                        onActivate {
                                            println("Some")
                                        }
                                    }
                            )
                            it.insertActionGroup("tray", this)
                        }
                    val menu = menu {
                        item("Item 1", "tray.action1")
                        item("Item 2", "tray.action2")
                    }
                    val popover = PopoverMenu.builder()
                        .setMenuModel(menu)
                        .build()
                    popMenu = popover
                    popover.parent = it
                }) {
                    popMenu?.popup()
                }

                var show by remember { mutableStateOf(false) }
                if (show) {
                    Label("Hello, world!")
                }

                PopoverMenu(
                    arrow = false,
                    trigger = {
                    Button(
                        "PopoverMenu composable",
                        modifier = Modifier.popoverTrigger(),
                        onClick = { popup() })
                }) {
                    menu {
                        section("First section") {
                            item("First item", "men.action1")
                            item("Second item", "men.action2")
                            item("Third item", "men.action3")
                            submenu("Submenu") {
                                item("Submenu item", "men.action1")
                            }
                        }
                        section("Second section") {
                            section("Inner section") {
                                item("Inner section item")
                            }
                            item("First item")
                            item("Second item")
                            item("Third item")
                        }
                        actionGroup("men") {
                            action("action1", state = Variant.boolean_(false)) { p ->
                                println("men.action1")
                                println(state)
                                state = Variant.boolean_(!(state?.boolean ?: false))
                                show = state?.boolean ?: false
                            }
                            action("action2") {
                                println("action2")
                            }
                            action("action3") {
                                println("action3")
                            }
                        }
                    }
                }
                Popover(trigger = {
                    Button(
                        "Popover composable",
                        modifier = Modifier.popoverTrigger(),
                        onClick = { popup() })
                }) {
                    Label("Hello, world!")
                }
            }
        }
    }
}


package io.github.mmarco94.compose.shared

import org.gnome.gio.SimpleAction
import org.gnome.gio.SimpleActionGroup
import org.gnome.glib.Variant
import org.gnome.glib.VariantType

interface ActionGroupBuilderScope {
    val actionGroup: SimpleActionGroup
}

fun actionGroup(builder: ActionGroupBuilderScope.() -> Unit): SimpleActionGroup = actionGroupBase(builder)

internal fun actionGroupBase(builder: ActionGroupBuilderScope.() -> Unit): SimpleActionGroup {
    val actionGroup = SimpleActionGroup.builder().build()
    val scope = object : ActionGroupBuilderScope {
        override val actionGroup: SimpleActionGroup = actionGroup
    }
    scope.builder()
    return actionGroup
}

fun ActionGroupBuilderScope.action(
    name: String,
    state: Variant? = null,
    parameterType: VariantType? = null,
    activate: SimpleAction.(parameter: Variant?) -> Unit,
) {
    val action = SimpleAction.builder()
        .setName(name)
        .apply {
            if (state != null) {
                setState(state)
            }
            if (parameterType != null) {
                setParameterType(parameterType)
            }
        }
        .build()
    action.onActivate { p ->
        activate(action, p)
    }
    actionGroup.addAction(action)
}
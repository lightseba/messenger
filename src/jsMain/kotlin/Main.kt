import react.child
import react.dom.render
import kotlinx.browser.document

import react.RBuilder
import react.RProps
import react.child
import react.dom.*
import react.functionalComponent
import react.router.dom.*

fun main() {
    render(document.getElementById("root")) {
        child(App)
    }
}

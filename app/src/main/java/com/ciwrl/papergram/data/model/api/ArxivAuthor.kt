package com.ciwrl.papergram.data.model.api

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "author", strict = false)
data class ArxivAuthor @JvmOverloads constructor(
    @field:Element(name = "name")
    var name: String = ""
)
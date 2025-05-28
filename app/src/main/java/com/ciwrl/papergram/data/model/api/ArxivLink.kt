package com.ciwrl.papergram.data.model.api

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Root

@Root(name = "link", strict = false)
data class ArxivLink @JvmOverloads constructor(
    @field:Attribute(name = "href")
    var href: String = "",

    @field:Attribute(name = "rel", required = false)
    var rel: String? = null,

    @field:Attribute(name = "title", required = false)
    var titleAttribute: String? = null,

    @field:Attribute(name = "type", required = false)
    var type: String? = null
)
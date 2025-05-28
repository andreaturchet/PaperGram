package com.ciwrl.papergram.data.model.api

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Root

@Root(name = "category", strict = false)
data class ArxivCategory @JvmOverloads constructor(
    @field:Attribute(name = "term")
    var term: String = ""
)
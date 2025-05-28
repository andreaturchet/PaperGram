package com.ciwrl.papergram.data.model.api

import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "feed", strict = false)
data class ArxivFeed @JvmOverloads constructor(
    @field:ElementList(name = "entry", inline = true, required = false)
    var entries: List<ArxivEntry>? = null
)
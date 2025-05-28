package com.ciwrl.papergram.data.model.api

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "entry", strict = false)
data class ArxivEntry @JvmOverloads constructor(
    @field:Element(name = "id")
    var id: String = "",

    @field:Element(name = "updated")
    var updated: String = "",

    @field:Element(name = "published")
    var publishedDate: String = "",

    @field:Element(name = "title")
    var title: String = "",

    @field:Element(name = "summary")
    var summary: String = "",

    @field:ElementList(name = "author", inline = true, required = false)
    var authors: List<ArxivAuthor>? = null,

    @field:ElementList(name = "link", inline = true, required = false)
    var links: List<ArxivLink>? = null,

    @field:ElementList(name = "category", inline = true, required = false)
    var categories: List<ArxivCategory>? = null
)
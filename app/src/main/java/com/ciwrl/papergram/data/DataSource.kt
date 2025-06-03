package com.ciwrl.papergram.data

import com.ciwrl.papergram.data.model.Category

object Datasource {
    fun getCategories(): List<Category> {
        return listOf(
            Category("Intelligenza Artificiale", "cs.AI"),
            Category("Machine Learning", "cs.LG"),
            Category("Visione Artificiale", "cs.CV"),
            Category("Calcolo e Linguaggio", "cs.CL"),
            Category("Robotica", "cs.RO"),
            Category("Grafica Computerizzata", "cs.GR"),
            Category("Sistemi Distribuiti", "cs.DC"),
            Category("Crittografia e Sicurezza", "cs.CR")
        )
    }
}
package com.ciwrl.papergram.data

import com.ciwrl.papergram.data.model.Category
import com.ciwrl.papergram.data.model.MainCategory

object Datasource {
    fun getMainCategories(): List<MainCategory> {
        return listOf(
            MainCategory("Computer Science", listOf(
                Category("Intelligenza Artificiale", "cs.AI"),
                Category("Hardware, Software e Architettura", "cs.AR"),
                Category("Complessità Computazionale", "cs.CC"),
                Category("Ingegneria Computazionale, Finanza e Scienza", "cs.CE"),
                Category("Geometria Computazionale", "cs.CG"),
                Category("Teoria dei Giochi e Informatica", "cs.GT"),
                Category("Visione Artificiale e Riconoscimento Pattern", "cs.CV"),
                Category("Computer e Società", "cs.CY"),
                Category("Crittografia e Sicurezza", "cs.CR"),
                Category("Basi di Dati", "cs.DB"),
                Category("Sistemi Distribuiti, Paralleli e Cluster", "cs.DC"),
                Category("Logica Digitale", "cs.DL"),
                Category("Strutture Dati e Algoritmi", "cs.DS"),
                Category("Tecnologie Emergenti", "cs.ET"),
                Category("Teoria Formale dei Linguaggi e degli Automi", "cs.FL"),
                Category("Grafica Computerizzata", "cs.GR"),
                Category("Interazione Uomo-Macchina", "cs.HC"),
                Category("Recupero Informazioni", "cs.IR"),
                Category("Apprendimento Automatico", "cs.LG"),
                Category("Linguaggi di Programmazione", "cs.PL"),
                Category("Robotica", "cs.RO"),
                Category("Ingegneria del Software", "cs.SE"),
                Category("Calcolo e Linguaggio", "cs.CL")
            )),
            MainCategory("Fisica", listOf(
                Category("Astrofisica", "astro-ph"),
                Category("Materia Condensata", "cond-mat"),
                Category("Relatività Generale e Cosmologia Quantistica", "gr-qc"),
                Category("Fisica delle Alte Energie (Esperimenti)", "hep-ex"),
                Category("Fisica delle Alte Energie (Teoria di Lattice)", "hep-lat"),
                Category("Fisica delle Alte Energie (Fenomenologia)", "hep-ph"),
                Category("Fisica delle Alte Energie (Teoria)", "hep-th"),
                Category("Fisica Matematica", "math-ph"),
                Category("Fisica Nucleare (Esperimenti)", "nucl-ex"),
                Category("Fisica Nucleare (Teoria)", "nucl-th"),
                Category("Fisica", "physics"),
                Category("Fotonica", "physics.optics"),
                Category("Fisica Quantistica", "quant-ph")
            )),
            MainCategory("Matematica", listOf(
                Category("Geometria Algebrica", "math.AG"),
                Category("Topologia Algebrica", "math.AT"),
                Category("Analisi delle PDE", "math.AP"),
                Category("Combinatoria", "math.CO"),
                Category("Geometria Differenziale", "math.DG"),
                Category("Sistemi Dinamici", "math.DS"),
                Category("Logica", "math.LO"),
                Category("Teoria dei Numeri", "math.NT"),
                Category("Teoria delle Probabilità", "math.PR"),
                Category("Statistica", "math.ST")
            )),
            MainCategory("Biologia", listOf(
                Category("Biomolecole", "q-bio.BM"),
                Category("Genomica", "q-bio.GN"),
                Category("Reti Neuronali e Cognizione", "q-bio.NC"),
                Category("Popolazioni ed Evoluzione", "q-bio.PE")
            )),
            MainCategory("Finanza", listOf(
                Category("Finanza Computazionale", "q-fin.CP"),
                Category("Finanza Generale", "q-fin.GN"),
                Category("Finanza Statistica", "q-fin.ST"),
                Category("Trading e Microstruttura del Mercato", "q-fin.TR")
            )),
            MainCategory("Statistica", listOf(
                Category("Applicazioni", "stat.AP"),
                Category("Calcolo", "stat.CO"),
                Category("Metodologia", "stat.ME"),
                Category("Apprendimento Automatico (Stat)", "stat.ML"),
                Category("Teoria della Statistica", "stat.TH")
            )),
            MainCategory("Ingegneria e Sistemi", listOf(
                Category("Elaborazione Audio e Vocale", "eess.AS"),
                Category("Elaborazione di Immagini e Video", "eess.IV"),
                Category("Elaborazione dei Segnali", "eess.SP"),
                Category("Sistemi e Controllo", "eess.SY")
            )),
            MainCategory("Economia", listOf(
                Category("Econometria", "econ.EM"),
                Category("Economia Generale", "econ.GN"),
                Category("Teoria Economica", "econ.TH")
            ))
        )
    }
}
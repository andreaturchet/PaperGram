package com.ciwrl.papergram.data

import com.ciwrl.papergram.data.model.Category
import com.ciwrl.papergram.data.model.Comment
import com.ciwrl.papergram.data.model.MainCategory

/**
 * Provides static, hard-coded data for the application.
 *
 * This object is used to supply initial data that is not fetched from an API,
 * such as the predefined list of ArXiv categories and fake comments for demonstration.
 */

object Datasource {
    /**
     * Returns the complete list of main categories and their sub-categories.
     */
    fun getMainCategories(): List<MainCategory> {
        return listOf(
            MainCategory("Computer Science","💻", listOf(
                Category("Artificial Intelligence", "cs.AI"),
                Category("Hardware, Software and Architecture", "cs.AR"),
                Category("Computational Complexity", "cs.CC"),
                Category("Computational Engineering, Finance, and Science", "cs.CE"),
                Category("Computational Geometry", "cs.CG"),
                Category("Computer Science and Game Theory", "cs.GT"),
                Category("Computer Vision and Pattern Recognition", "cs.CV"),
                Category("Computers and Society", "cs.CY"),
                Category("Cryptography and Security", "cs.CR"),
                Category("Databases", "cs.DB"),
                Category("Distributed, Parallel, and Cluster Computing", "cs.DC"),
                Category("Digital Libraries", "cs.DL"),
                Category("Data Structures and Algorithms", "cs.DS"),
                Category("Emerging Technologies", "cs.ET"),
                Category("Formal Languages and Automata Theory", "cs.FL"),
                Category("Graphics", "cs.GR"),
                Category("Human-Computer Interaction", "cs.HC"),
                Category("Information Retrieval", "cs.IR"),
                Category("Machine Learning", "cs.LG"),
                Category("Programming Languages", "cs.PL"),
                Category("Robotics", "cs.RO"),
                Category("Software Engineering", "cs.SE"),
                Category("Computation and Language", "cs.CL")
            )),
            MainCategory("Physics","⚛️", listOf(
                Category("Astrophysics", "astro-ph"),
                Category("Condensed Matter", "cond-mat"),
                Category("General Relativity and Quantum Cosmology", "gr-qc"),
                Category("High Energy Physics - Experiment", "hep-ex"),
                Category("High Energy Physics - Lattice", "hep-lat"),
                Category("High Energy Physics - Phenomenology", "hep-ph"),
                Category("High Energy Physics - Theory", "hep-th"),
                Category("Mathematical Physics", "math-ph"),
                Category("Nuclear Experiment", "nucl-ex"),
                Category("Nuclear Theory", "nucl-th"),
                Category("Physics", "physics"),
                Category("Optics", "physics.optics"),
                Category("Quantum Physics", "quant-ph")
            )),
            MainCategory("Mathematics","🧮", listOf(
                Category("Algebraic Geometry", "math.AG"),
                Category("Algebraic Topology", "math.AT"),
                Category("Analysis of PDEs", "math.AP"),
                Category("Combinatorics", "math.CO"),
                Category("Differential Geometry", "math.DG"),
                Category("Dynamical Systems", "math.DS"),
                Category("Logic", "math.LO"),
                Category("Number Theory", "math.NT"),
                Category("Probability", "math.PR"),
                Category("Statistics", "math.ST")
            )),
            MainCategory("Biology","🧬", listOf(
                Category("Biomolecules", "q-bio.BM"),
                Category("Genomics", "q-bio.GN"),
                Category("Neurons and Cognition", "q-bio.NC"),
                Category("Populations and Evolution", "q-bio.PE")
            )),
            MainCategory("Finance","📈", listOf(
                Category("Computational Finance", "q-fin.CP"),
                Category("General Finance", "q-fin.GN"),
                Category("Statistical Finance", "q-fin.ST"),
                Category("Trading and Market Microstructure", "q-fin.TR")
            )),
            MainCategory("Statistics","📊", listOf(
                Category("Applications", "stat.AP"),
                Category("Computation", "stat.CO"),
                Category("Methodology", "stat.ME"),
                Category("Machine Learning", "stat.ML"),
                Category("Statistics Theory", "stat.TH")
            )),
            MainCategory("Engineering and Systems","⚙️", listOf(
                Category("Audio and Speech Processing", "eess.AS"),
                Category("Image and Video Processing", "eess.IV"),
                Category("Signal Processing", "eess.SP"),
                Category("Systems and Control", "eess.SY")
            )),
            MainCategory("Economy","🏛️", listOf(
                Category("Econometrics", "econ.EM"),
                Category("General Economics", "econ.GN"),
                Category("Theoretical Economics", "econ.TH")
            ))
        )
    }

    fun getFakeComments(): List<Comment> {
        /**
         * Returns a list of fake comments for placeholder or demonstration purposes.
         */
        val now = System.currentTimeMillis()
        val fakeTopLevel = Comment(
            id = "fake_1",
            userName = "Marco Rossi",
            userAvatarUrl = "",
            timestamp = "Yesterday",
            commentText = "Questo paper è fondamentale per chiunque lavori in questo campo. Un punto di riferimento."
        )
        val fakeReply = Comment(
            id = "fake_2",
            userName = "Giulia Bianchi",
            userAvatarUrl = "",
            timestamp = "2 hours ago",
            commentText = "Concordo pienamente! L'ho citato nella mia tesi."
        )
        return listOf(fakeTopLevel, fakeReply)
    }
}
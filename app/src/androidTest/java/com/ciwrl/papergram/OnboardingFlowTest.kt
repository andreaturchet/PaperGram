package com.ciwrl.papergram

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI Test for the complete user onboarding flow.
 */
@RunWith(AndroidJUnit4::class)
class OnboardingFlowTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(OnboardingActivity::class.java)

    @Test
    fun onboardingFlow_fromNameEntry_toInterestSelection_isSuccessful() {
        // --- Schermata 1: Inserimento Nome ---

        // Arrange: Defininsce il nome da inserire
        val userName = "Andrea"

        // Act:
        // 1. Trova la view con l'ID 'editText_name'
        // 2. Scrivi il testo 'Andrea' al suo interno
        onView(withId(R.id.editText_name)).perform(typeText(userName))

        // 3. Trova la view con l'ID 'button_continue'
        // 4. Esegui un'azione di click
        onView(withId(R.id.button_continue)).perform(click())

        // Assert:
        // Ora siamo nella seconda schermata.
        // Verifica che la view con il testo "Scegli i tuoi interessi" sia visibile.
        onView(withId(R.id.textView_title_interest)).check(matches(isDisplayed()))

        // Verifica anche che il bottone "FATTO" sia visibile
        onView(withId(R.id.button_finish)).check(matches(isDisplayed()))
    }
}
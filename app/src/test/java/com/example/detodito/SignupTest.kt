package com.example.detodito

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.detodito.databinding.ActivitySignupBinding
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
class SignupTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(Signup::class.java)

    @Test
    fun test_registerButton() {
        // Preparación
        val activityScenario = activityRule.scenario
        activityScenario.onActivity { activity ->
            val dialog = activity.dialog
            val email = "test@mail.com"
            val password = "123456"
            val phone = "1234567890"
            val firstName = "John"
            val lastName = "Doe"
            val doc = "123456789"
            val address = "Test address"
            val dept = "Test department"
            val ciudad = "Test city"
            activity.binding.emailEditText.setText(email)
            activity.binding.passwordEditText.setText(password)
            activity.binding.celularEditText.setText(phone)
            activity.binding.firstNameEditText.setText(firstName)
            activity.binding.lastNameEditText.setText(lastName)
            activity.binding.docEditText.setText(doc)
            activity.binding.addressEditText.setText(address)
            activity.binding.autoCompleteTextView.setText(dept)
            activity.binding.ciudadautoCompleteTextView.setText(ciudad)
        }

        // Acción
        onView(withId(R.id.register_button)).perform(click())

        // Verificación
        onView(withText("Cargando...")).check(matches(isDisplayed()))
        Thread.sleep(5000) // Esperar a que la llamada a la API termine
        onView(withText("Cuenta creada correctamente")).check(matches(isDisplayed()))
    }

    @Test
    fun onCreate_Success() {
        val activityController = Robolectric.buildActivity(Signup::class.java)
        val activity = activityController.create().visible().get()

        assertNotNull(activity.binding)
        assertNotNull(activity.job)
        assertNotNull(activity.coroutineScope)
        assertEquals("", activity.department)
        assertNotNull(activity.dialog)
    }

    // Agregar más pruebas si son necesarias
}
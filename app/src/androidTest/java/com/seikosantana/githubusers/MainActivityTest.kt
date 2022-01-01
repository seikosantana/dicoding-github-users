package com.seikosantana.githubusers

import android.view.KeyEvent
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityTest {

    @Test
    fun trySearchForTenSeconds() {
        onView(withId(R.id.searchViewSearchUser)).perform(typeText("seikosantana"), pressKey(
            KeyEvent.KEYCODE_ENTER))

        Thread.sleep(10000)
        onView(withId(R.id.rvUserList)).check(matches(isDisplayed()))
    }

    @Test
    fun tryLoadingIndicator() {
        onView(withId(R.id.searchViewSearchUser)).perform(typeText("seikosantana"), pressKey(
            KeyEvent.KEYCODE_ENTER))
        onView(withId(R.id.progressSearch)).check(matches(isDisplayed()))
    }

    @Before
    fun createActivity() {
        ActivityScenario.launch(MainActivity::class.java)
    }
}
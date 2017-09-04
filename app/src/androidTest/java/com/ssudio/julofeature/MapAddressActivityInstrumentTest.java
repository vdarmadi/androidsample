package com.ssudio.julofeature;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.UiThreadTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MapAddressActivityInstrumentTest {
    @Rule
    public ActivityTestRule<MyAddressActivity> mActivityRule =
            new ActivityTestRule(MyAddressActivity.class);

    @Rule public UiThreadTestRule threadTestRule = new UiThreadTestRule();

    private GoogleMap googleMap;
    private CountingIdlingResource countingIdlingResource = new CountingIdlingResource("MapReady");

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.ssudio.julofeature", appContext.getPackageName());
    }

    @Before
    public void beforeEach() throws Throwable {
        countingIdlingResource.increment();

        final MapFragment mapFragment = mActivityRule.getActivity().getMapFragment();

        final OnMapReadyCallback onMapReadyCallback = new OnMapReadyCallback() {
            @Override public void onMapReady(GoogleMap googleMap) {
                countingIdlingResource.decrement();

                MapAddressActivityInstrumentTest.this.googleMap = googleMap;
            }
        };

        threadTestRule.runOnUiThread(new Runnable() {
            @Override public void run() {
                mapFragment.getMapAsync(onMapReadyCallback);
            }
        });

        boolean idlingResource = Espresso.registerIdlingResources(countingIdlingResource);

        assertThat(idlingResource, anyOf(is(true)));
    }

    @After
    public void afterEach() {
        assertThat(Espresso.unregisterIdlingResources(countingIdlingResource), anyOf(is(true)));
    }

    @Test
    public void mapShouldDisplayed() {
        onView(withId(R.id.userMap)).check(matches(isDisplayed()));
    }

    @Test
    public void mapShouldShowMarkerOnLoad() throws Throwable {
        final MarkerOptions[] center = {null};

        threadTestRule.runOnUiThread(new Runnable() {
            @Override public void run() {
                center[0] = new MarkerOptions()
                        .position(googleMap.getCameraPosition().target);

                googleMap.addMarker(center[0]);
            }
        });

        MarkerOptions expected = new MarkerOptions()
                .position(new LatLng(-8.3405, 115.092));

        assertThat(String.format("%.4f", center[0].getPosition().latitude),
                anyOf(is(String.format("%.4f", expected.getPosition().latitude))));
    }
}

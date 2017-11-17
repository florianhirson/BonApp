package fr.hirsonf.stage.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import fr.hirsonf.stage.R;

/**
 * Created by flohi on 06/11/2017.
 */

public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note here that we DO NOT use setContentView();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        //addSlide(firstFragment);
        //addSlide(secondFragment);
        //addSlide(thirdFragment);
        //addSlide(fourthFragment);

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        addSlide(AppIntroFragment.newInstance("Titre 1! ", "Euh ?", "Description 1!!! ", "WTF",
                R.drawable.yes,
                getColor(R.color.colorBg),
                getColor(R.color.colorTitle),
                getColor(R.color.colorDesc)));

        addSlide(AppIntroFragment.newInstance("Titre 2! ", "Euh ?", "Description 2!!! ", "WTF",
                R.drawable.yes,
                getColor(R.color.colorBg),
                getColor(R.color.colorTitle),
                getColor(R.color.colorDesc)));

        addSlide(AppIntroFragment.newInstance("Titre 3! ", "Euh ?", "Description 3!!! ", "WTF",
                R.drawable.yes,
                getColor(R.color.colorBg),
                getColor(R.color.colorTitle),
                getColor(R.color.colorDesc)));

        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip/Done button.
        showSkipButton(false);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        setVibrate(true);
        setVibrateIntensity(30);
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        final Intent i = new Intent(IntroActivity.this, LoginActivity.class);
        startActivity(i);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        final Intent i = new Intent(IntroActivity.this, LoginActivity.class);
        startActivity(i);
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}

package com.evgenii.maptest;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends AppCompatActivity {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        Intent intent = new Intent(this, MapContainerActivity.class);
//        startActivity(intent);
//        overridePendingTransition(0, 0);
//        finish();
//    }

    /**
     * Whether or not we're showing the back of the card (otherwise showing the front).
     */
    private boolean mShowingBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getFragmentManager()
                .beginTransaction()
                .add(R.id.container, new CardFrontFragment())
                .commit();

    }

    public void didTapFlipButton(View view) {
        flipCard();
    }

    private void flipCard() {
        int enterAnimation;
        int exitAnimation;
        int enterAnimationPop;
        int exitAnimationPop;
        Fragment fragment;

        if (mShowingBack) {
            enterAnimation = R.animator.card_flip_left_in;
            exitAnimation = R.animator.card_flip_left_out;
            enterAnimationPop = R.animator.card_flip_right_in;
            exitAnimationPop = R.animator.card_flip_right_out;
            fragment = new CardFrontFragment();
        } else {
            enterAnimation = R.animator.card_flip_right_in;
            exitAnimation = R.animator.card_flip_right_out;
            enterAnimationPop = R.animator.card_flip_left_in;
            exitAnimationPop = R.animator.card_flip_left_out;
            fragment = new CardBackFragment();
        }

        mShowingBack = !mShowingBack;

        getFragmentManager()
            .beginTransaction()
            .setCustomAnimations(enterAnimation, exitAnimation, enterAnimationPop, exitAnimationPop)
            .replace(R.id.container, fragment)
            .commit();
    }

    /**
     * A fragment representing the front of the card.
     */
    public static class CardFrontFragment extends Fragment {
        public CardFrontFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_card_front, container, false);
        }
    }

    /**
     * A fragment representing the back of the card.
     */
    public static class CardBackFragment extends Fragment {
        public CardBackFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_card_back, container, false);
        }
    }
}

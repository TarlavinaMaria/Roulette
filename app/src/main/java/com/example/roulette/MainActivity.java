package com.example.roulette;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button spinBtn;

    EditText betInput;
    EditText betAmountInput;
    TextView resultText;
    TextView balanceText;
    RadioGroup colorGroup;

    RadioButton betRed;
    RadioButton betBlack;
    Random random;
    int lastDegree = 0;

    int balance = 1000;
    int betNumber = -1;
    final int[] redNumbers = {32, 19, 21, 25, 34, 27, 36, 30, 23, 5, 16, 1, 14, 9, 18, 7, 12, 3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.rouletteWheel);
        spinBtn = findViewById(R.id.spinBtn);
        betInput = findViewById(R.id.betInput);
        resultText = findViewById(R.id.resultText);
        betAmountInput = findViewById(R.id.betAmountInput);
        balanceText = findViewById(R.id.balanceText);
        random = new Random();
        colorGroup = findViewById(R.id.colorGroup);
        betRed = findViewById(R.id.betRed);
        betBlack = findViewById(R.id.betBlack);

        balanceText.setText("Баланс " + balance);

        spinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinRoulette();
            }
        });
    }

    private void spinRoulette() {
        String betStr = betInput.getText().toString();
        String betAmountStr = betAmountInput.getText().toString();

        if (betStr.isEmpty() || betAmountStr.isEmpty()) {
            resultText.setText("Введите число и сумму для ставки");
            return;
        }

        int betAmount = Integer.parseInt(betAmountStr);


        if (betAmount > balance) {
            resultText.setText("Недостаточно средств");
            return;
        }

        if (!betStr.isEmpty()) {
            betNumber = Integer.parseInt(betStr);
            if (betNumber < 0 || betNumber > 36) {
                resultText.setText("Введите число от 0 до 36");
                return;
            }
        }

        boolean betOnRed = betRed.isChecked();
        boolean betOnBlack = betBlack.isChecked();

        if (betNumber == -1 && !betOnRed && !betOnBlack) {
            resultText.setText("Введите число или цвет для ставки");
            return;
        }

        balance -= betAmount;
        balanceText.setText("Баланс " + balance);
        int spinAngle = random.nextInt(360) + 360 * 5;
        RotateAnimation rotate = new RotateAnimation(lastDegree, spinAngle, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(3000);
        rotate.setFillAfter(true);
        rotate.setInterpolator(new DecelerateInterpolator());
        lastDegree = spinAngle % 360;
        rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                resultText.setText("Крутится...");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                showResult(betNumber, betAmount, betOnRed, betOnBlack);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        imageView.startAnimation(rotate);
    }

    private void showResult(int betNumber, int betAmount, boolean betOnRed, boolean betOnBlack) {
        int resultNumber = (36 - (lastDegree / 10)) % 37;

        String resulMessage = "Выпало число " + resultNumber;

        boolean isRed = isNumberRed(resultNumber);
        boolean isBlack = !isRed && resultNumber != 0;

        boolean numberWin = (resultNumber == betNumber);
        boolean colorWin = (betOnRed && isRed) || (betOnBlack && isBlack);

        if (numberWin) {
            int winnings = betAmount * 36;
            balance += winnings;
            resulMessage += ". Вы выиграли";
        } else if (colorWin) {
            int winnings = betAmount * 2;
            balance += winnings;
            resulMessage += ". Вы выиграли";
        } else {
            resulMessage += ". Вы проиграли";
        }
        resultText.setText(resulMessage);
        balanceText.setText("Баланс " + balance);
    }

    public boolean isNumberRed(int number) {
        for (int red : redNumbers) {
            if (red == number) return true;
        }
        return false;
    }
}
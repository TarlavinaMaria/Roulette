package com.example.roulette;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    // Объявление переменных для элементов интерфейса
    ImageView imageView;
    Button spinBtn;

    // Поля для ввода данных
    EditText betInput;
    EditText betAmountInput;
    TextView resultText;
    TextView balanceText;
    RadioGroup colorGroup;

    // Радиокнопки
    RadioButton betRed;       // Кнопка "Красное"
    RadioButton betBlack;     // Кнопка "Черное"

    Random random;           // Генератор случайных чисел
    int lastDegree = 0;      // Последний угол поворота колеса

    int balance = 1000;      // Начальный баланс игрока
    int betNumber = -1;      // Число ставки (-1 означает отсутствие ставки на число)

    // Массив красных чисел на рулетке
    final int[] redNumbers = {32, 19, 21, 25, 34, 27, 36, 30, 23, 5, 16, 1, 14, 9, 18, 7, 12, 3};

    // Объявление переменных для таблицы результатов
    private TableLayout resultsTable;
    private int spinCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация элементов интерфейса
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
        resultsTable = findViewById(R.id.resultsTable);

        balanceText.setText("Баланс: " + balance);

        // Обработчик нажатия на кнопку "Крутить"
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

        // Проверка введена ли сумма ставки
        if (betAmountStr.isEmpty()) {
            resultText.setText("Введите сумму для ставки");
            return;
        }

        int betAmount = Integer.parseInt(betAmountStr);

        // Проверка что сумма ставки положительна
        if (betAmount <= 0) {
            resultText.setText("Сумма ставки должна быть больше 0");
            return;
        }

        // Проверка что у игрока достаточно средств
        if (betAmount > balance) {
            resultText.setText("Недостаточно средств");
            return;
        }

        // Проверяем какие цвета выбраны
        boolean betOnRed = betRed.isChecked();
        boolean betOnBlack = betBlack.isChecked();

        // Обработка ввода числа ставки
        if (!betStr.isEmpty()) {
            try {
                betNumber = Integer.parseInt(betStr);
                // Проверка что число в диапазоне 0-36
                if (betNumber < 0 || betNumber > 36) {
                    resultText.setText("Введите число от 0 до 36");
                    return;
                }
            } catch (NumberFormatException e) {
                resultText.setText("Введите корректное число");
                return;
            }
        } else {
            betNumber = -1;  // Если число не введено
        }

        // Проверка что хотя бы что-то выбрано
        if (betNumber == -1 && !betOnRed && !betOnBlack) {
            resultText.setText("Введите число или выберите цвет для ставки");
            return;
        }

        // Вычитаем сумму ставки из баланса
        balance -= betAmount;
        balanceText.setText("Баланс: " + balance);

        // Генерируем случайный угол вращения (5-6 полных оборотов)
        int spinAngle = random.nextInt(360) + 360 * 5;

        // Создаем анимацию вращения
        RotateAnimation rotate = new RotateAnimation(
                lastDegree,
                spinAngle,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );

        // Настройка анимации
        rotate.setDuration(3000);
        rotate.setFillAfter(true);
        // Интерполятор для эффекта замедления в конце
        rotate.setInterpolator(new DecelerateInterpolator());
        lastDegree = spinAngle % 360;

        // Обработчики анимации
        rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                spinBtn.setEnabled(false);  // Блокируем кнопку во время вращения
                resultText.setText("Крутится...");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                spinBtn.setEnabled(true);  // Разблокируем кнопку
                // Показываем результат
                showResult(betNumber, betAmount, betOnRed, betOnBlack);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        imageView.startAnimation(rotate);
    }

    private void addResultToTable(int resultNumber, boolean isRed, boolean isBlack, int betAmount, int winnings) {
        spinCount++;

        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        // № крутки
        TextView spinNumber = createTableCell(String.valueOf(spinCount));

        // Выпавшее число
        TextView number = createTableCell(String.valueOf(resultNumber));

        // Цвет
        TextView color = new TextView(this);
        if (resultNumber == 0) {
            color.setText("Зеленый");
            color.setTextColor(Color.GREEN);
        } else if (isRed) {
            color.setText("Красный");
            color.setTextColor(Color.RED);
        } else {
            color.setText("Черный");
            color.setTextColor(Color.BLACK);
        }
        color = styleTableCell(color);

        // Сумма ставки
        TextView bet = createTableCell(betAmount + "$");

        // Результат
        TextView result = new TextView(this);
        if (winnings > 0) {
            result.setText("+" + winnings + "$");
            result.setTextColor(Color.GREEN);
        } else {
            result.setText("-" + betAmount + "$");
            result.setTextColor(Color.RED);
        }
        result = styleTableCell(result);

        // Добавляем ячейки в строку
        row.addView(spinNumber);
        row.addView(number);
        row.addView(color);
        row.addView(bet);
        row.addView(result);

        // Добавляем строку в таблицу
        resultsTable.addView(row);

        // Ограничиваем количество записей (например, 20)
        if (resultsTable.getChildCount() > 20) {
            resultsTable.removeViewAt(1); // Удаляем самую старую запись (после заголовка)
        }
    }

    private TextView createTableCell(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        return styleTableCell(textView);
    }

    private TextView styleTableCell(TextView textView) {
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(5, 5, 5, 5);
        return textView;
    }

    private void showResult(int betNumber, int betAmount, boolean betOnRed, boolean betOnBlack) {
        // Вычисляем выпавшее число (0-36)
        int resultNumber = (36 - (lastDegree / 10)) % 37;
        String resultMessage = "Выпало число: " + resultNumber;

        // Определяем цвет выпавшего числа
        boolean isRed = isNumberRed(resultNumber);
        boolean isBlack = !isRed && resultNumber != 0;
        boolean isZero = resultNumber == 0;

        // Проверяем выигрышные комбинации
        boolean numberWin = (resultNumber == betNumber);
        boolean colorWin = false;
        int winnings = 0;

        // Проверка выигрыша по числу
        if (numberWin) {
            winnings = betAmount * 36;  // Выигрыш 35:1
            resultMessage += "\nПоздравляем! Вы угадали число!";
        }
        // Проверка выигрыша по цвету (если не выпал 0)
        else if (!isZero) {
            if ((betOnRed && isRed) || (betOnBlack && isBlack)) {
                colorWin = true;
                winnings = betAmount * 2;  // Выигрыш 1:1
                resultMessage += "\nПоздравляем! Вы угадали цвет!";
            }
        }

        // Обновление баланса если выигрыш
        if (numberWin || colorWin) {
            balance += winnings;
            resultMessage += "\nВыигрыш: " + winnings;
        } else {
            resultMessage += "\nВы проиграли ставку";
        }

        // Добавляем запись в таблицу результатов
        addResultToTable(resultNumber, isRed, isBlack, betAmount, winnings);

        // Сброс полей для новой ставки
        betInput.setText("");
        betAmountInput.setText("");
        colorGroup.clearCheck();
        this.betNumber = -1;

        // Обновление интерфейса
        resultText.setText(resultMessage);
        balanceText.setText("Баланс: " + balance);
    }

    // Проверка принадлежит ли число к красным
    private boolean isNumberRed(int number) {
        for (int red : redNumbers) {
            if (red == number) return true;
        }
        return false;
    }
}
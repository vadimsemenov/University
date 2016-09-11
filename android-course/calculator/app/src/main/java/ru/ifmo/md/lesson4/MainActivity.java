package ru.ifmo.md.lesson4;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.Toast;


public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    private static final String[] BUTTONS = {
            "7", "8", "9", "×",
            "4", "5", "6", "÷",
            "1", "2", "3", "+",
            "0", ".", "E", "-",
            "(", ")", "delete", "="
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button buttonC = (Button) findViewById(R.id.C);
        final EditText editText = (EditText) findViewById(R.id.editText);
        final CalculationEngine calculationEngine = new Calculator();
        buttonC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
            }
        });
        final GridView gridView = (GridView) findViewById(R.id.gridView);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, BUTTONS);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            private static final String TAG = "anon OnItemClickListener";
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.i(TAG, "position = " + position + "; id = " + id + "; view: " + view.getId());
                if (position == 19) { // =
                    String expression = editText.getText().toString().replaceAll("×", "*").replaceAll("÷", "/").replaceAll("∞", "Infinity");
                    String result = "smth went wrong :(";
                    try {
                        result = Double.toString(calculationEngine.calculate(expression)).replace("Infinity", "∞");
                    } catch (CalculationException e) {
                        Toast.makeText(MainActivity.this, e.getMessage() == null ? "some error occur" : e.getMessage(), Toast.LENGTH_SHORT);
                    }
                    editText.setText(result);
                } else if (position == 3) {
                    editText.append("×");
                } else if (position == 7) {
                    editText.append("÷");
                } else if (position == 18) {
                    String expression = editText.getText().toString();
                    if (expression.endsWith("NaN")) {
                        expression = expression.substring(0, expression.length() - 3);
                    } else {
                        expression = expression.substring(0, expression.length() - 1);
                    }
                    editText.setText(expression);
                } else {
                    editText.append(BUTTONS[position]);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.maina, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

package ru.ifmo.md.lesson4;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button button = (Button) findViewById(R.id.button);
        final EditText editText = (EditText) findViewById(R.id.editText);
        final CalculationEngine calculationEngine = new Calculator();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String expression = editText.getText().toString();
                Log.d(TAG, "expression is '" + expression + "'");
                String result = "smth went wrong :(";
                try {
                    result = String.valueOf(calculationEngine.calculate(expression));
                } catch (CalculationException e) {
                    Log.e(TAG, e.getMessage() == null ? "empty message" : e.getMessage());
                    // Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG);
                }
                editText.setText(result);
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

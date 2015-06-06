package shahharshil46.myappportfolio;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements View.OnClickListener{

    Button btnApp1, btnApp2, btnApp3, btnApp4, btnApp5, btnApp6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnApp1 = (Button) findViewById(R.id.btn_app1);
        btnApp2 = (Button) findViewById(R.id.btn_app2);
        btnApp3 = (Button) findViewById(R.id.btn_app3);
        btnApp4 = (Button) findViewById(R.id.btn_app4);
        btnApp5 = (Button) findViewById(R.id.btn_app5);
        btnApp6 = (Button) findViewById(R.id.btn_app6);

        btnApp1.setOnClickListener(this);
        btnApp2.setOnClickListener(this);
        btnApp3.setOnClickListener(this);
        btnApp4.setOnClickListener(this);
        btnApp5.setOnClickListener(this);
        btnApp6.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "work in Progress...!!!",1000).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn_app1:
                Toast.makeText(this, "This button will launch my "+getText(R.string.text_spotify_streamer), 1000).show();
                break;

            case R.id.btn_app2:
                Toast.makeText(this, "This button will launch my "+getText(R.string.text_scores_app), 1000).show();
                break;

            case R.id.btn_app3:
                Toast.makeText(this, "This button will launch my "+getText(R.string.text_library_app), 1000).show();
                break;

            case R.id.btn_app4:
                Toast.makeText(this, "This button will launch my "+getText(R.string.text_build_it_bigger), 1000).show();
                break;

            case R.id.btn_app5:
                Toast.makeText(this, "This button will launch my "+getText(R.string.text_xyz_reader), 1000).show();
                break;

            case R.id.btn_app6:
                Toast.makeText(this, "This button will launch my "+getText(R.string.text_capstone_my_own_app), 1000).show();
                break;
        }
    }
}

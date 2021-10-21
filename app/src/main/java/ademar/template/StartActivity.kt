package ademar.template

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val action = intent?.action
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("INITIAL_ACTION", action)
        startActivity(intent)
        finish()
    }

}

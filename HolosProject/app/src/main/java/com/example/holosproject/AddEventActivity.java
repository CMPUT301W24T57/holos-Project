import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.holosproject.R;

public class AddEventActivity extends AppCompatActivity {

    private EditText eventNameEditText;
    private EditText eventDateEditText;
    private EditText eventTimeEditText;
    private EditText eventLocationEditText;
    private EditText eventDescriptionEditText;
    private Button cancelButton;
    private Button saveButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        eventNameEditText = findViewById(R.id.edit_text_event_name);
        eventDateEditText = findViewById(R.id.edit_text_event_date);
        eventTimeEditText = findViewById(R.id.edit_text_event_time);
        eventLocationEditText = findViewById(R.id.edit_text_event_address);
        eventDescriptionEditText = findViewById(R.id.edit_text_event_description);
        cancelButton = findViewById(R.id.button_cancel);
        saveButton = findViewById(R.id.button_save);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventName = eventNameEditText.getText().toString();
                String eventDate = eventDateEditText.getText().toString();
                String eventTime = eventTimeEditText.getText().toString();
                String eventLocation = eventLocationEditText.getText().toString();
                String eventDescription = eventDescriptionEditText.getText().toString();

                Event newEvent = new Event(eventName, eventDate, eventTime, eventLocation, eventDescription);

                Intent resultIntent = new Intent();
                resultIntent.putExtra("event", newEvent);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}

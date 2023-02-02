/**
 * Copyright (c) 2012-2017, Smart Engines Ltd
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * <p>
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the Smart Engines Ltd nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.smartengines;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity for sample recognition results view
 */
public class MainResultView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        CustomList adapter = new CustomList(
                MainResultView.this, MainResultStore.instance.getFieldNames());
        ListView list = findViewById(R.id.list);
        list.setAdapter(adapter);

        for (String name : MainResultStore.instance.getFieldNames()) {
            Log.e("Field Name", name);
        }
    }

    public class CustomList extends ArrayAdapter<String> {

        private final Activity context;
        private final String[] fieldNames;

        public CustomList(Activity context, String[] fieldNames) {
            super(context, R.layout.result_row, fieldNames);
            this.context = context;
            this.fieldNames = fieldNames;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {

            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.result_row, null, true);

            String fieldName = fieldNames[position];
            TextView label = rowView.findViewById(R.id.label);

            if (position == 0) {  // show document type at first position
                String message = String.format(
                        "Document type: \t%s", MainResultStore.instance.getDocumentType());
                label.setText(message);
                return rowView;
            }

            String message =
                    (MainResultStore.instance.isFieldAccepted(fieldName) ? "[+] " : "[-] ")
                            + fieldName;

            if (MainResultStore.instance.isFieldImage(fieldName)) {
                label.setText(message);

                ImageView imageView = rowView.findViewById(R.id.image);
                imageView.setImageBitmap(MainResultStore.instance.getImageValue(fieldName));
            } else {
                message = message + ":\t" + MainResultStore.instance.getStringValue(fieldName);
                label.setText(message);
            }

            return rowView;
        }
    }
}
